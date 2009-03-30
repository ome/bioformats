//
// FilterMetadata.vm
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
 * Created by melissa via MetadataAutogen on Jan 5, 2009 1:43:34 PM CST
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

  /* @see MetadataStore#setCircleID(String, int, int, int) */
  public void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setCircleID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCirclecx(String, int, int, int) */
  public void setCirclecx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cx) : cx;
    store.setCirclecx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCirclecy(String, int, int, int) */
  public void setCirclecy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cy) : cy;
    store.setCirclecy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCircler(String, int, int, int) */
  public void setCircler(String r, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(r) : r;
    store.setCircler(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setCircletransform(String, int, int, int) */
  public void setCircletransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setCircletransform(value, imageIndex, roiIndex, shapeIndex);
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

  /* @see MetadataStore#setDetectorAmplificationGain(Float, int, int) */
  public void setDetectorAmplificationGain(Float amplificationGain, int instrumentIndex, int detectorIndex) {
    store.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
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

  /* @see MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
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

  /* @see MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    store.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
  }

  /* @see MetadataStore#setDetectorZoom(Float, int, int) */
  public void setDetectorZoom(Float zoom, int instrumentIndex, int detectorIndex) {
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

  /* @see MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsReadOutRate(Float, int, int) */
  public void setDetectorSettingsReadOutRate(Float readOutRate, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex);
  }

  /* @see MetadataStore#setDetectorSettingsVoltage(Float, int, int) */
  public void setDetectorSettingsVoltage(Float voltage, int imageIndex, int logicalChannelIndex) {
    store.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex);
  }

  // -- Dichroic property storage -

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

  /* @see MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    store.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
  }

  /* @see MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
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

  /* @see MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
    store.setDisplayOptionsZoom(zoom, imageIndex);
  }

  // -- DisplayOptionsProjection property storage -

  /* @see MetadataStore#setDisplayOptionsProjectionZStart(Integer, int) */
  public void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex) {
    store.setDisplayOptionsProjectionZStart(zStart, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsProjectionZStop(Integer, int) */
  public void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex) {
    store.setDisplayOptionsProjectionZStop(zStop, imageIndex);
  }

  // -- DisplayOptionsTime property storage -

  /* @see MetadataStore#setDisplayOptionsTimeTStart(Integer, int) */
  public void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex) {
    store.setDisplayOptionsTimeTStart(tStart, imageIndex);
  }

  /* @see MetadataStore#setDisplayOptionsTimeTStop(Integer, int) */
  public void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex) {
    store.setDisplayOptionsTimeTStop(tStop, imageIndex);
  }

  // -- Ellipse property storage -

  /* @see MetadataStore#setEllipseID(String, int, int, int) */
  public void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setEllipseID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipsecx(String, int, int, int) */
  public void setEllipsecx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cx) : cx;
    store.setEllipsecx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipsecy(String, int, int, int) */
  public void setEllipsecy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cy) : cy;
    store.setEllipsecy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipserx(String, int, int, int) */
  public void setEllipserx(String rx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(rx) : rx;
    store.setEllipserx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipsery(String, int, int, int) */
  public void setEllipsery(String ry, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(ry) : ry;
    store.setEllipsery(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setEllipsetransform(String, int, int, int) */
  public void setEllipsetransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setEllipsetransform(value, imageIndex, roiIndex, shapeIndex);
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

  // -- GreyChannel property storage -

  /* @see MetadataStore#setGreyChannelBlackLevel(Float, int) */
  public void setGreyChannelBlackLevel(Float blackLevel, int imageIndex) {
    store.setGreyChannelBlackLevel(blackLevel, imageIndex);
  }

  /* @see MetadataStore#setGreyChannelChannelNumber(Integer, int) */
  public void setGreyChannelChannelNumber(Integer channelNumber, int imageIndex) {
    store.setGreyChannelChannelNumber(channelNumber, imageIndex);
  }

  /* @see MetadataStore#setGreyChannelGamma(Float, int) */
  public void setGreyChannelGamma(Float gamma, int imageIndex) {
    store.setGreyChannelGamma(gamma, imageIndex);
  }

  /* @see MetadataStore#setGreyChannelWhiteLevel(Float, int) */
  public void setGreyChannelWhiteLevel(Float whiteLevel, int imageIndex) {
    store.setGreyChannelWhiteLevel(whiteLevel, imageIndex);
  }

  /* @see MetadataStore#setGreyChannelisOn(Boolean, int) */
  public void setGreyChannelisOn(Boolean isOn, int imageIndex) {
    store.setGreyChannelisOn(isOn, imageIndex);
  }

  // -- GreyChannelMap property storage -

  /* @see MetadataStore#setGreyChannelMapColorMap(String, int) */
  public void setGreyChannelMapColorMap(String colorMap, int imageIndex) {
    String value = filter ? DataTools.sanitize(colorMap) : colorMap;
    store.setGreyChannelMapColorMap(value, imageIndex);
  }

  // -- Group property storage -

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

  /* @see MetadataStore#setImageObjective(String, int) */
  public void setImageObjective(String objective, int imageIndex) {
    String value = filter ? DataTools.sanitize(objective) : objective;
    store.setImageObjective(value, imageIndex);
  }

  // -- ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    store.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    store.setImagingEnvironmentHumidity(humidity, imageIndex);
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
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

  /* @see MetadataStore#setLaserRepetitionRate(Boolean, int, int) */
  public void setLaserRepetitionRate(Boolean repetitionRate, int instrumentIndex, int lightSourceIndex) {
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

  /* @see MetadataStore#setLightSourcePower(Float, int, int) */
  public void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex) {
    store.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
  }

  /* @see MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    String value = filter ? DataTools.sanitize(serialNumber) : serialNumber;
    store.setLightSourceSerialNumber(value, instrumentIndex, lightSourceIndex);
  }

  // -- LightSourceRef property storage -

  /* @see MetadataStore#setLightSourceRefAttenuation(Float, int, int, int) */
  public void setLightSourceRefAttenuation(Float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
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

  /* @see MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
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

  /* @see MetadataStore#setLinetransform(String, int, int, int) */
  public void setLinetransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setLinetransform(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLinex1(String, int, int, int) */
  public void setLinex1(String x1, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x1) : x1;
    store.setLinex1(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLinex2(String, int, int, int) */
  public void setLinex2(String x2, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x2) : x2;
    store.setLinex2(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLiney1(String, int, int, int) */
  public void setLiney1(String y1, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y1) : y1;
    store.setLiney1(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setLiney2(String, int, int, int) */
  public void setLiney2(String y2, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y2) : y2;
    store.setLiney2(value, imageIndex, roiIndex, shapeIndex);
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

  /* @see MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
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

  /* @see MetadataStore#setLogicalChannelPinholeSize(Float, int, int) */
  public void setLogicalChannelPinholeSize(Float pinholeSize, int imageIndex, int logicalChannelIndex) {
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

  /* @see MetadataStore#setMaskID(String, int, int, int) */
  public void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setMaskID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskheight(String, int, int, int) */
  public void setMaskheight(String height, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(height) : height;
    store.setMaskheight(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMasktransform(String, int, int, int) */
  public void setMasktransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setMasktransform(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskwidth(String, int, int, int) */
  public void setMaskwidth(String width, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(width) : width;
    store.setMaskwidth(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskx(String, int, int, int) */
  public void setMaskx(String x, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x) : x;
    store.setMaskx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMasky(String, int, int, int) */
  public void setMasky(String y, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y) : y;
    store.setMasky(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- MaskPixels property storage -

  /* @see MetadataStore#setMaskPixelsBigEndian(Boolean, int, int, int) */
  public void setMaskPixelsBigEndian(Boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex) {
    store.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setMaskPixelsBinData(String, int, int, int) */
  public void setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(binData) : binData;
    store.setMaskPixelsBinData(value, imageIndex, roiIndex, shapeIndex);
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

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
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

  /* @see MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
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

  /* @see MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
  }

  // -- ObjectiveSettings property storage -

  /* @see MetadataStore#setObjectiveSettingsCorrectionCollar(Float, int) */
  public void setObjectiveSettingsCorrectionCollar(Float correctionCollar, int imageIndex) {
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

  /* @see MetadataStore#setObjectiveSettingsRefractiveIndex(Float, int) */
  public void setObjectiveSettingsRefractiveIndex(Float refractiveIndex, int imageIndex) {
    store.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex);
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

  /* @see MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
  }

  // -- Plate property storage -

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

  /* @see MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
    String value = filter ? DataTools.sanitize(status) : status;
    store.setPlateStatus(value, plateIndex);
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

  /* @see MetadataStore#setPointID(String, int, int, int) */
  public void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPointID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointcx(String, int, int, int) */
  public void setPointcx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cx) : cx;
    store.setPointcx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointcy(String, int, int, int) */
  public void setPointcy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(cy) : cy;
    store.setPointcy(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointr(String, int, int, int) */
  public void setPointr(String r, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(r) : r;
    store.setPointr(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPointtransform(String, int, int, int) */
  public void setPointtransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setPointtransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Polygon property storage -

  /* @see MetadataStore#setPolygonID(String, int, int, int) */
  public void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPolygonID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolygonpoints(String, int, int, int) */
  public void setPolygonpoints(String points, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(points) : points;
    store.setPolygonpoints(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolygontransform(String, int, int, int) */
  public void setPolygontransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setPolygontransform(value, imageIndex, roiIndex, shapeIndex);
  }

  // -- Polyline property storage -

  /* @see MetadataStore#setPolylineID(String, int, int, int) */
  public void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setPolylineID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylinepoints(String, int, int, int) */
  public void setPolylinepoints(String points, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(points) : points;
    store.setPolylinepoints(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setPolylinetransform(String, int, int, int) */
  public void setPolylinetransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setPolylinetransform(value, imageIndex, roiIndex, shapeIndex);
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

  /* @see MetadataStore#setRectID(String, int, int, int) */
  public void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setRectID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectheight(String, int, int, int) */
  public void setRectheight(String height, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(height) : height;
    store.setRectheight(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRecttransform(String, int, int, int) */
  public void setRecttransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(transform) : transform;
    store.setRecttransform(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectwidth(String, int, int, int) */
  public void setRectwidth(String width, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(width) : width;
    store.setRectwidth(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRectx(String, int, int, int) */
  public void setRectx(String x, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(x) : x;
    store.setRectx(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setRecty(String, int, int, int) */
  public void setRecty(String y, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(y) : y;
    store.setRecty(value, imageIndex, roiIndex, shapeIndex);
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

  /* @see MetadataStore#setShapeID(String, int, int, int) */
  public void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    String value = filter ? DataTools.sanitize(id) : id;
    store.setShapeID(value, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapetheT(Integer, int, int, int) */
  public void setShapetheT(Integer theT, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapetheT(theT, imageIndex, roiIndex, shapeIndex);
  }

  /* @see MetadataStore#setShapetheZ(Integer, int, int, int) */
  public void setShapetheZ(Integer theZ, int imageIndex, int roiIndex, int shapeIndex) {
    store.setShapetheZ(theZ, imageIndex, roiIndex, shapeIndex);
  }

  // -- StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    String value = filter ? DataTools.sanitize(name) : name;
    store.setStageLabelName(value, imageIndex);
  }

  /* @see MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    store.setStageLabelX(x, imageIndex);
  }

  /* @see MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    store.setStageLabelY(y, imageIndex);
  }

  /* @see MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    store.setStageLabelZ(z, imageIndex);
  }

  // -- StagePosition property storage -

  /* @see MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
  }

  /* @see MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    store.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
  }

  // -- Thumbnail property storage -

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

  /* @see MetadataStore#setThumbnailhref(String, int) */
  public void setThumbnailhref(String href, int imageIndex) {
    String value = filter ? DataTools.sanitize(href) : href;
    store.setThumbnailhref(value, imageIndex);
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

  /* @see MetadataStore#setWellSamplePosX(Float, int, int, int) */
  public void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex) {
    store.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
  }

  /* @see MetadataStore#setWellSamplePosY(Float, int, int, int) */
  public void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex) {
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
