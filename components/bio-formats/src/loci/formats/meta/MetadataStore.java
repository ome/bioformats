//
// MetadataStore.java
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
 * Created by melissa via MetadataAutogen on Jun 19, 2009 1:03:12 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A proxy whose responsibility it is to marshal biological image data into a
 * particular storage medium.
 *
 * <p>The <code>MetadataStore</code> interface encompasses the metadata that
 * any specific storage medium (file, relational database, etc.) should be
 * expected to store into its backing data model.
 *
 * <p>The <code>MetadataStore</code> interface goes hand in hand with the
 * <code>MetadataRetrieve</code> interface. Essentially,
 * <code>MetadataRetrieve</code> provides the "getter" methods for a storage
 * medium, and <code>MetadataStore</code> provides the "setter" methods.
 *
 * <p>Since it often makes sense for a storage medium to implement both
 * interfaces, there is also an {@link IMetadata} interface encompassing
 * both <code>MetadataStore</code> and <code>MetadataRetrieve</code>, which
 * reduces the need to cast between object types.
 *
 * <p>See {@link loci.formats.ome.OMEXMLMetadata} for an example
 * implementation.
 *
 * <p><b>Important note:</b> It is strongly recommended that applications
 * (e.g., file format readers) using <code>MetadataStore</code> populate
 * information in a linear order. Specifically, iterating over entities
 * from "leftmost" index to "rightmost" index is required for certain
 * <code>MetadataStore</code> implementations such as OMERO's
 * <code>OMEROMetadataStore</code>. For example, when populating Image, Pixels
 * and Plane information, an outer loop should iterate across
 * <code>imageIndex</code>, an inner loop should iterate across
 * <code>pixelsIndex</code>, and an innermost loop should handle
 * <code>planeIndex</code>. For an illustration of the ideal traversal order,
 * see {@link loci.formats.meta.MetadataConverter#convertMetadata}.</p>
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/MetadataStore.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/MetadataStore.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataStore {

  void createRoot();

  Object getRoot();

  void setRoot(Object root);

  // - Entity storage -

  /** Sets the UUID associated with this collection of metadata. */
  void setUUID(String uuid);

  // - Arc property storage -

  /**
   * For a particular Arc, sets the type of arc.
   * @param type the type of arc
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setArcType(String type, int instrumentIndex, int lightSourceIndex);

  // - ChannelComponent property storage -

  /**
   * For a particular ChannelComponent, sets which color channel this ChannelComponent belongs to (for example, 'R' for an 'RGB' PhotometricInterpretation).
   * @param colorDomain which color channel this ChannelComponent belongs to (for example, 'R' for an 'RGB' PhotometricInterpretation)
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  /**
   * For a particular ChannelComponent, sets the index into the channel dimension of the 5-D pixel array.
   * @param index the index into the channel dimension of the 5-D pixel array
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  /**
   * For a particular ChannelComponent, sets the pixels to which this channel component corresponds.
   * @param pixels the pixels to which this channel component corresponds
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  // - Circle property storage -

  /**
   * For a particular Circle, sets X coordinate of the center of the circle.
   * @param cx X coordinate of the center of the circle
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, sets Y coordinate of the center of the circle.
   * @param cy Y coordinate of the center of the circle
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, sets unique label identifying the circle.
   * @param id unique label identifying the circle
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, sets radius of the circle, in pixels.
   * @param r radius of the circle, in pixels
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  // - Contact property storage -

  /**
   * For a particular Contact, sets unique label identifying the experimenter who is the group contact.
   * @param experimenter unique label identifying the experimenter who is the group contact
   * @param groupIndex index of the Group
   */
  void setContactExperimenter(String experimenter, int groupIndex);

  // - Dataset property storage -

  /**
   * For a particular Dataset, sets description of the dataset.
   * @param description description of the dataset
   * @param datasetIndex index of the Dataset
   */
  void setDatasetDescription(String description, int datasetIndex);

  /**
   * For a particular Dataset, sets reference to the experimenter who owns the dataset.
   * @param experimenterRef reference to the experimenter who owns the dataset
   * @param datasetIndex index of the Dataset
   */
  void setDatasetExperimenterRef(String experimenterRef, int datasetIndex);

  /**
   * For a particular Dataset, sets reference to the group that owns the dataset.
   * @param groupRef reference to the group that owns the dataset
   * @param datasetIndex index of the Dataset
   */
  void setDatasetGroupRef(String groupRef, int datasetIndex);

  /**
   * For a particular Dataset, sets unique label identifying the dataset.
   * @param id unique label identifying the dataset
   * @param datasetIndex index of the Dataset
   */
  void setDatasetID(String id, int datasetIndex);

  /**
   * For a particular Dataset, sets indicates whether the images in the dataset can be altered.
   * @param locked indicates whether the images in the dataset can be altered
   * @param datasetIndex index of the Dataset
   */
  void setDatasetLocked(Boolean locked, int datasetIndex);

  /**
   * For a particular Dataset, sets name of the dataset.
   * @param name name of the dataset
   * @param datasetIndex index of the Dataset
   */
  void setDatasetName(String name, int datasetIndex);

  // - DatasetRef property storage -

  /**
   * For a particular DatasetRef, sets TODO.
   * @param id TODO
   * @param imageIndex index of the Image
   * @param datasetRefIndex index of the DatasetRef
   */
  void setDatasetRefID(String id, int imageIndex, int datasetRefIndex);

  // - Detector property storage -

  /**
   * For a particular Detector, sets amplification gain of the detector.
   * @param amplificationGain amplification gain of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorAmplificationGain(Float amplificationGain, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets the gain of the detector.
   * @param gain the gain of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets unique label identifying the detector.
   * @param id unique label identifying the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorID(String id, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets name of the detector manufacturer.
   * @param manufacturer name of the detector manufacturer
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets model name/number of the detector.
   * @param model model name/number of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets the offset of the detector.
   * @param offset the offset of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets serial number of the detector.
   * @param serialNumber serial number of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets the detector type, e.g. CCD, PMT.
   * @param type the detector type, e.g. CCD, PMT
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorType(String type, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets the voltage of the detector.
   * @param voltage the voltage of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, sets optional zoom value of the detector.
   * @param zoom optional zoom value of the detector
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  void setDetectorZoom(Float zoom, int instrumentIndex, int detectorIndex);

  // - DetectorSettings property storage -

  /**
   * For a particular DetectorSettings, sets the detector binning.
   * @param binning the detector binning
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the detector associated with this channel.
   * @param detector the detector associated with this channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the detector gain.
   * @param gain the detector gain
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the detector offset.
   * @param offset the detector offset
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the speed at which the detector can count pixels, in MHz.
   * @param readOutRate the speed at which the detector can count pixels, in MHz
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsReadOutRate(Float readOutRate, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, sets the detector voltage.
   * @param voltage the detector voltage
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setDetectorSettingsVoltage(Float voltage, int imageIndex, int logicalChannelIndex);

  // - Dichroic property storage -

  /**
   * For a particular Dichroic, sets TODO.
   * @param lotNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param dichroicIndex index of the Dichroic
   */
  void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex);

  /**
   * For a particular Dichroic, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param dichroicIndex index of the Dichroic
   */
  void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex);

  /**
   * For a particular Dichroic, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param dichroicIndex index of the Dichroic
   */
  void setDichroicModel(String model, int instrumentIndex, int dichroicIndex);

  // - Dimensions property storage -

  /**
   * For a particular Dimensions, sets the size of an individual pixel's X axis in microns.
   * @param physicalSizeX the size of an individual pixel's X axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the size of an individual pixel's Y axis in microns.
   * @param physicalSizeY the size of an individual pixel's Y axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the size of an individual pixel's Z axis in microns.
   * @param physicalSizeZ the size of an individual pixel's Z axis in microns
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the distance between adjacent time points in seconds.
   * @param timeIncrement the distance between adjacent time points in seconds
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the distance between adjacent wavelengths in nanometers.
   * @param waveIncrement the distance between adjacent wavelengths in nanometers
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, sets the starting wavelength in nanometers.
   * @param waveStart the starting wavelength in nanometers
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex);

  // - DisplayOptions property storage -

  /**
   * For a particular DisplayOptions, sets specifies to display the image as grayscale or RGB.
   * @param display specifies to display the image as grayscale or RGB
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsDisplay(String display, int imageIndex);

  /**
   * For a particular DisplayOptions, sets unique label identifying the display options.
   * @param id unique label identifying the display options
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsID(String id, int imageIndex);

  /**
   * For a particular DisplayOptions, sets zoom factor for use in the display (NOT THE LENS ZOOM).
   * @param zoom zoom factor for use in the display (NOT THE LENS ZOOM)
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsZoom(Float zoom, int imageIndex);

  // - DisplayOptionsProjection property storage -

  /**
   * For a particular DisplayOptionsProjection, sets the first focal plane to include in the maximum intensity projection.
   * @param zStart the first focal plane to include in the maximum intensity projection
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsProjectionZStart(Integer zStart, int imageIndex);

  /**
   * For a particular DisplayOptionsProjection, sets the last focal plane to include in the maximum intensity projection.
   * @param zStop the last focal plane to include in the maximum intensity projection
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsProjectionZStop(Integer zStop, int imageIndex);

  // - DisplayOptionsTime property storage -

  /**
   * For a particular DisplayOptionsTime, sets the first time point to include in the animation.
   * @param tStart the first time point to include in the animation
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsTimeTStart(Integer tStart, int imageIndex);

  /**
   * For a particular DisplayOptionsTime, sets the last time point to include in the animation.
   * @param tStop the last time point to include in the animation
   * @param imageIndex index of the Image
   */
  void setDisplayOptionsTimeTStop(Integer tStop, int imageIndex);

  // - Ellipse property storage -

  /**
   * For a particular Ellipse, sets X coordinate of the center of the ellipse.
   * @param cx X coordinate of the center of the ellipse
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, sets Y coordinate of the center of the ellipse.
   * @param cy Y coordinate of the center of the ellipse
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, sets unique label identifying the ellipse.
   * @param id unique label identifying the ellipse
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, sets horizontal radius of the ellipse.
   * @param rx horizontal radius of the ellipse
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, sets vertical radius of the ellipse.
   * @param ry vertical radius of the ellipse
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  // - EmFilter property storage -

  /**
   * For a particular EmFilter, sets TODO.
   * @param lotNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

  /**
   * For a particular EmFilter, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

  /**
   * For a particular EmFilter, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setEmFilterModel(String model, int instrumentIndex, int filterIndex);

  /**
   * For a particular EmFilter, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setEmFilterType(String type, int instrumentIndex, int filterIndex);

  // - ExFilter property storage -

  /**
   * For a particular ExFilter, sets TODO.
   * @param lotNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

  /**
   * For a particular ExFilter, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

  /**
   * For a particular ExFilter, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setExFilterModel(String model, int instrumentIndex, int filterIndex);

  /**
   * For a particular ExFilter, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setExFilterType(String type, int instrumentIndex, int filterIndex);

  // - Experiment property storage -

  /**
   * For a particular Experiment, sets description of the experiment.
   * @param description description of the experiment
   * @param experimentIndex index of the Experiment
   */
  void setExperimentDescription(String description, int experimentIndex);

  /**
   * For a particular Experiment, sets ID of the experimenter who conducted the experiment.
   * @param experimenterRef ID of the experimenter who conducted the experiment
   * @param experimentIndex index of the Experiment
   */
  void setExperimentExperimenterRef(String experimenterRef, int experimentIndex);

  /**
   * For a particular Experiment, sets unique label identifying the experiment.
   * @param id unique label identifying the experiment
   * @param experimentIndex index of the Experiment
   */
  void setExperimentID(String id, int experimentIndex);

  /**
   * For a particular Experiment, sets type of experiment, e.g. FRET, TimeLapse.
   * @param type type of experiment, e.g. FRET, TimeLapse
   * @param experimentIndex index of the Experiment
   */
  void setExperimentType(String type, int experimentIndex);

  // - Experimenter property storage -

  /**
   * For a particular Experimenter, sets the e-mail address of the experimenter.
   * @param email the e-mail address of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterEmail(String email, int experimenterIndex);

  /**
   * For a particular Experimenter, sets the first name of the experimenter.
   * @param firstName the first name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterFirstName(String firstName, int experimenterIndex);

  /**
   * For a particular Experimenter, sets unique label identifying the experimenter.
   * @param id unique label identifying the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterID(String id, int experimenterIndex);

  /**
   * For a particular Experimenter, sets the institution to which the experimenter belongs.
   * @param institution the institution to which the experimenter belongs
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterInstitution(String institution, int experimenterIndex);

  /**
   * For a particular Experimenter, sets the last name of the experimenter.
   * @param lastName the last name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterLastName(String lastName, int experimenterIndex);

  /**
   * For a particular Experimenter, sets OME user name of the experimenter.
   * @param omeName OME user name of the experimenter
   * @param experimenterIndex index of the Experimenter
   */
  void setExperimenterOMEName(String omeName, int experimenterIndex);

  // - ExperimenterMembership property storage -

  /**
   * For a particular ExperimenterMembership, sets the group associated with this membership.
   * @param group the group associated with this membership
   * @param experimenterIndex index of the Experimenter
   * @param groupRefIndex index of the GroupRef
   */
  void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex);

  // - Filament property storage -

  /**
   * For a particular Filament, sets the type of filament.
   * @param type the type of filament
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setFilamentType(String type, int instrumentIndex, int lightSourceIndex);

  // - Filter property storage -

  /**
   * For a particular Filter, sets TODO.
   * @param filterWheel TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, sets unique label identifying the filter.
   * @param id unique label identifying the filter
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setFilterID(String id, int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, sets TODO.
   * @param lotNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setFilterModel(String model, int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, sets TODO.
   * @param type TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setFilterType(String type, int instrumentIndex, int filterIndex);

  // - FilterSet property storage -

  /**
   * For a particular FilterSet, sets TODO.
   * @param dichroic TODO
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, sets TODO.
   * @param emFilter TODO
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, sets TODO.
   * @param exFilter TODO
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, sets unique label identifying the filter set.
   * @param id unique label identifying the filter set
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetID(String id, int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, sets TODO.
   * @param lotNumber TODO
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, sets TODO.
   * @param manufacturer TODO
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, sets TODO.
   * @param model TODO
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex);

  // - GreyChannel property storage -

  /**
   * For a particular GreyChannel, sets dimmest pixel value; anything below this will be set to 0.
   * @param blackLevel dimmest pixel value; anything below this will be set to 0
   * @param imageIndex index of the Image
   */
  void setGreyChannelBlackLevel(Float blackLevel, int imageIndex);

  /**
   * For a particular GreyChannel, sets actual channel number, indexed from 0.
   * @param channelNumber actual channel number, indexed from 0
   * @param imageIndex index of the Image
   */
  void setGreyChannelChannelNumber(Integer channelNumber, int imageIndex);

  /**
   * For a particular GreyChannel, sets gamma value for this channel.
   * @param gamma gamma value for this channel
   * @param imageIndex index of the Image
   */
  void setGreyChannelGamma(Float gamma, int imageIndex);

  /**
   * For a particular GreyChannel, sets brightest pixel value; anything above this will be set to 255.
   * @param whiteLevel brightest pixel value; anything above this will be set to 255
   * @param imageIndex index of the Image
   */
  void setGreyChannelWhiteLevel(Float whiteLevel, int imageIndex);

  /**
   * For a particular GreyChannel, sets TODO.
   * @param isOn TODO
   * @param imageIndex index of the Image
   */
  void setGreyChannelisOn(Boolean isOn, int imageIndex);

  // - GreyChannelMap property storage -

  /**
   * For a particular GreyChannelMap, sets describes color mapping function - Greyscale, Spectrum, or Blackbody.
   * @param colorMap describes color mapping function - Greyscale, Spectrum, or Blackbody
   * @param imageIndex index of the Image
   */
  void setGreyChannelMapColorMap(String colorMap, int imageIndex);

  // - Group property storage -

  /**
   * For a particular Group, sets the name of the group.
   * @param name the name of the group
   * @param groupIndex index of the Group
   */
  void setGroupName(String name, int groupIndex);

  // - GroupRef property storage -

  // - Image property storage -

  /**
   * For a particular Image, sets acquired pixels set for the image.
   * @param acquiredPixels acquired pixels set for the image
   * @param imageIndex index of the Image
   */
  void setImageAcquiredPixels(String acquiredPixels, int imageIndex);

  /**
   * For a particular Image, sets the creation date of the image.
   * @param creationDate the creation date of the image
   * @param imageIndex index of the Image
   */
  void setImageCreationDate(String creationDate, int imageIndex);

  /**
   * For a particular Image, sets default pixels set for the image.
   * @param defaultPixels default pixels set for the image
   * @param imageIndex index of the Image
   */
  void setImageDefaultPixels(String defaultPixels, int imageIndex);

  /**
   * For a particular Image, sets the full description of the image.
   * @param description the full description of the image
   * @param imageIndex index of the Image
   */
  void setImageDescription(String description, int imageIndex);

  /**
   * For a particular Image, sets reference to the experiment to which this image belongs.
   * @param experimentRef reference to the experiment to which this image belongs
   * @param imageIndex index of the Image
   */
  void setImageExperimentRef(String experimentRef, int imageIndex);

  /**
   * For a particular Image, sets reference to the experimenter who created this image.
   * @param experimenterRef reference to the experimenter who created this image
   * @param imageIndex index of the Image
   */
  void setImageExperimenterRef(String experimenterRef, int imageIndex);

  /**
   * For a particular Image, sets reference to the group that owns this image.
   * @param groupRef reference to the group that owns this image
   * @param imageIndex index of the Image
   */
  void setImageGroupRef(String groupRef, int imageIndex);

  /**
   * For a particular Image, sets unique label identifying the image.
   * @param id unique label identifying the image
   * @param imageIndex index of the Image
   */
  void setImageID(String id, int imageIndex);

  /**
   * For a particular Image, sets label reference for the associated instrument.
   * @param instrumentRef label reference for the associated instrument
   * @param imageIndex index of the Image
   */
  void setImageInstrumentRef(String instrumentRef, int imageIndex);

  /**
   * For a particular Image, sets the full name of the image.
   * @param name the full name of the image
   * @param imageIndex index of the Image
   */
  void setImageName(String name, int imageIndex);

  /**
   * For a particular Image, sets reference to the objective used when acquiring this image.
   * @param objective reference to the objective used when acquiring this image
   * @param imageIndex index of the Image
   */
  void setImageObjective(String objective, int imageIndex);

  // - ImagingEnvironment property storage -

  /**
   * For a particular ImagingEnvironment, sets air pressure, in millibars.
   * @param airPressure air pressure, in millibars
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex);

  /**
   * For a particular ImagingEnvironment, sets CO2 level, in percent fractions from 0.0 to 1.0.
   * @param cO2Percent CO2 level, in percent fractions from 0.0 to 1.0
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex);

  /**
   * For a particular ImagingEnvironment, sets humidity, in percent fractions from 0.0 to 1.0.
   * @param humidity humidity, in percent fractions from 0.0 to 1.0
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentHumidity(Float humidity, int imageIndex);

  /**
   * For a particular ImagingEnvironment, sets temperature of the imaging environment, in Celsius.
   * @param temperature temperature of the imaging environment, in Celsius
   * @param imageIndex index of the Image
   */
  void setImagingEnvironmentTemperature(Float temperature, int imageIndex);

  // - Instrument property storage -

  /**
   * For a particular Instrument, sets unique label identifying the instrument.
   * @param id unique label identifying the instrument
   * @param instrumentIndex index of the Instrument
   */
  void setInstrumentID(String id, int instrumentIndex);

  // - Laser property storage -

  /**
   * For a particular Laser, sets value by which frequency is multiplied.
   * @param frequencyMultiplication value by which frequency is multiplied
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets lasing medium for the laser.
   * @param laserMedium lasing medium for the laser
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param pockelCell TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets TODO.
   * @param pulse TODO
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets rate in Hz at which the laser pulses.
   * @param repetitionRate rate in Hz at which the laser pulses
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserRepetitionRate(Boolean repetitionRate, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets indicates whether or not the laser can be tuned.
   * @param tuneable indicates whether or not the laser can be tuned
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets type of laser - Excimer, Gas, MetalVapor, SolidState, Dye, SemiConductor, FreeElectron, or Unknown.
   * @param type type of laser - Excimer, Gas, MetalVapor, SolidState, Dye, SemiConductor, FreeElectron, or Unknown
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserType(String type, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, sets wavelength of the laser, in nm.
   * @param wavelength wavelength of the laser, in nm
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex);

  // - LightSource property storage -

  /**
   * For a particular LightSource, sets unique label identifying the light source.
   * @param id unique label identifying the light source
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets manufacturer of the light source.
   * @param manufacturer manufacturer of the light source
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets model number of the light source.
   * @param model model number of the light source
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets the light source power, in watts.
   * @param power the light source power, in watts
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, sets serial number of the light source.
   * @param serialNumber serial number of the light source
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

  // - LightSourceRef property storage -

  /**
   * For a particular LightSourceRef, sets the primary light source attenuation.
   * @param attenuation the primary light source attenuation
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param lightSourceRefIndex index of the LightSourceRef
   */
  void setLightSourceRefAttenuation(Float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

  /**
   * For a particular LightSourceRef, sets TODO.
   * @param lightSource TODO
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param lightSourceRefIndex index of the LightSourceRef
   */
  void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

  /**
   * For a particular LightSourceRef, sets the primary light source wavelength.
   * @param wavelength the primary light source wavelength
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param lightSourceRefIndex index of the LightSourceRef
   */
  void setLightSourceRefWavelength(Integer wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

  // - LightSourceSettings property storage -

  /**
   * For a particular LightSourceSettings, sets the primary light source attenuation.
   * @param attenuation the primary light source attenuation
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, sets the primary light source.
   * @param lightSource the primary light source
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, sets the primary light source wavelength.
   * @param wavelength the primary light source wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex);

  // - Line property storage -

  /**
   * For a particular Line, sets unique label identifying the line.
   * @param id unique label identifying the line
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, sets X coordinate of the first endpoint of the line.
   * @param x1 X coordinate of the first endpoint of the line
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, sets X coordinate of the second endpoint of the line.
   * @param x2 X coordinate of the second endpoint of the line
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, sets Y coordinate of the first endpoint of the line.
   * @param y1 Y coordinate of the first endpoint of the line
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, sets Y coordinate of the second endpoint of the line.
   * @param y2 Y coordinate of the second endpoint of the line
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex);

  // - LogicalChannel property storage -

  /**
   * For a particular LogicalChannel, sets the constrast method name.
   * @param contrastMethod the constrast method name
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets reference to the detector used to acquire this logical channel.
   * @param detector reference to the detector used to acquire this logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the emission wavelength.
   * @param emWave the emission wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the excitation wavelength.
   * @param exWave the excitation wavelength
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets reference to the filter set associated with this channel.
   * @param filterSet reference to the filter set associated with this channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the fluorescence type.
   * @param fluor the fluorescence type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets unique label identifying the logical channel.
   * @param id unique label identifying the logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the illumination type.
   * @param illuminationType the illumination type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets reference to the light source used to acquire this logical channel.
   * @param lightSource reference to the light source used to acquire this logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the acquisition mode.
   * @param mode the acquisition mode
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the logical channel's name.
   * @param name the logical channel's name
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the neutral-density filter value.
   * @param ndFilter the neutral-density filter value
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the OTF associated with the logical channel.
   * @param otf the OTF associated with the logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the photometric interpretation type.
   * @param photometricInterpretation the photometric interpretation type
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets the size of the pinhole.
   * @param pinholeSize the size of the pinhole
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPinholeSize(Float pinholeSize, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets TODO.
   * @param pockelCellSetting TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets number of channel components in the logical channel.
   * @param samplesPerPixel number of channel components in the logical channel
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets TODO.
   * @param secondaryEmissionFilter TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, sets TODO.
   * @param secondaryExcitationFilter TODO
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex);

  // - Mask property storage -

  /**
   * For a particular Mask, sets TODO.
   * @param height TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, sets unique label identifying the mask.
   * @param id unique label identifying the mask
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, sets TODO.
   * @param width TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, sets TODO.
   * @param x TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, sets TODO.
   * @param y TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex);

  // - MaskPixels property storage -

  /**
   * For a particular MaskPixels, sets TODO.
   * @param bigEndian TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskPixelsBigEndian(Boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, sets TODO.
   * @param binData TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskPixelsBinData(String binData, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, sets TODO.
   * @param extendedPixelType TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, sets unique label identifying the mask's pixels.
   * @param id unique label identifying the mask's pixels
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, sets TODO.
   * @param sizeX TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskPixelsSizeX(Integer sizeX, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, sets TODO.
   * @param sizeY TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setMaskPixelsSizeY(Integer sizeY, int imageIndex, int roiIndex, int shapeIndex);

  // - MicrobeamManipulation property storage -

  /**
   * For a particular MicrobeamManipulation, sets reference to the experimenter who applied this manipulation.
   * @param experimenterRef reference to the experimenter who applied this manipulation
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   */
  void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex);

  /**
   * For a particular MicrobeamManipulation, sets unique label identifying the microbeam manipulation.
   * @param id unique label identifying the microbeam manipulation
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   */
  void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex);

  /**
   * For a particular MicrobeamManipulation, sets type of operation - FRAP, Photoablation, Photoactivation, Uncaging, OpticalTrapping, or Other.
   * @param type type of operation - FRAP, Photoablation, Photoactivation, Uncaging, OpticalTrapping, or Other
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   */
  void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex);

  // - MicrobeamManipulationRef property storage -

  /**
   * For a particular MicrobeamManipulationRef, sets TODO.
   * @param id TODO
   * @param experimentIndex index of the Experiment
   * @param microbeamManipulationRefIndex index of the MicrobeamManipulationRef
   */
  void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex);

  // - Microscope property storage -

  /**
   * For a particular Microscope, sets unique label identifying the microscope.
   * @param id unique label identifying the microscope
   * @param instrumentIndex index of the Instrument
   */
  void setMicroscopeID(String id, int instrumentIndex);

  /**
   * For a particular Microscope, sets manufacturer of the microscope.
   * @param manufacturer manufacturer of the microscope
   * @param instrumentIndex index of the Instrument
   */
  void setMicroscopeManufacturer(String manufacturer, int instrumentIndex);

  /**
   * For a particular Microscope, sets model number of the microscope.
   * @param model model number of the microscope
   * @param instrumentIndex index of the Instrument
   */
  void setMicroscopeModel(String model, int instrumentIndex);

  /**
   * For a particular Microscope, sets serial number of the microscope.
   * @param serialNumber serial number of the microscope
   * @param instrumentIndex index of the Instrument
   */
  void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex);

  /**
   * For a particular Microscope, sets microscope type - Upright, Inverted, Dissection, Electrophysiology, or Unknown.
   * @param type microscope type - Upright, Inverted, Dissection, Electrophysiology, or Unknown
   * @param instrumentIndex index of the Instrument
   */
  void setMicroscopeType(String type, int instrumentIndex);

  // - OTF property storage -

  /**
   * For a particular OTF, sets the Base64-encoded optical transfer function, or the path to a file containing it.
   * @param binaryFile the Base64-encoded optical transfer function, or the path to a file containing it
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets unique label identifying the optical transfer function.
   * @param id unique label identifying the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFID(String id, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets objective described by the optical transfer function.
   * @param objective objective described by the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFObjective(String objective, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets indicates whether or not optical axis averaging was performed.
   * @param opticalAxisAveraged indicates whether or not optical axis averaging was performed
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets the pixel type of the optical transfer function.
   * @param pixelType the pixel type of the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets width of the optical transfer function.
   * @param sizeX width of the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, sets height of the optical transfer function.
   * @param sizeY height of the optical transfer function
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex);

  // - Objective property storage -

  /**
   * For a particular Objective, sets the measured magnification of the objective.
   * @param calibratedMagnification the measured magnification of the objective
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets type of correction coating applied to the objective lens.
   * @param correction type of correction coating applied to the objective lens
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets unique label identifying the objective.
   * @param id unique label identifying the objective
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets immersion medium used with the objective lens.
   * @param immersion immersion medium used with the objective lens
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets TODO.
   * @param iris TODO
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets numerical aperture of the lens.
   * @param lensNA numerical aperture of the lens
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets the name of the objective's manufacturer.
   * @param manufacturer the name of the objective's manufacturer
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets model name/number of the objective.
   * @param model model name/number of the objective
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets the specified magnification of the objective.
   * @param nominalMagnification the specified magnification of the objective
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets serial number of the objective.
   * @param serialNumber serial number of the objective
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, sets working distance of the objective, in um.
   * @param workingDistance working distance of the objective, in um
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex);

  // - ObjectiveSettings property storage -

  /**
   * For a particular ObjectiveSettings, sets unit-less setting of the adjustable correction collar.
   * @param correctionCollar unit-less setting of the adjustable correction collar
   * @param imageIndex index of the Image
   */
  void setObjectiveSettingsCorrectionCollar(Float correctionCollar, int imageIndex);

  /**
   * For a particular ObjectiveSettings, sets immersion medium for the lens.
   * @param medium immersion medium for the lens
   * @param imageIndex index of the Image
   */
  void setObjectiveSettingsMedium(String medium, int imageIndex);

  /**
   * For a particular ObjectiveSettings, sets the objective associated with this image.
   * @param objective the objective associated with this image
   * @param imageIndex index of the Image
   */
  void setObjectiveSettingsObjective(String objective, int imageIndex);

  /**
   * For a particular ObjectiveSettings, sets unit-less refractive index of the immersion medium.
   * @param refractiveIndex unit-less refractive index of the immersion medium
   * @param imageIndex index of the Image
   */
  void setObjectiveSettingsRefractiveIndex(Float refractiveIndex, int imageIndex);

  // - Path property storage -

  /**
   * For a particular Path, sets EXPERIMENTAL.
   * @param d EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Path, sets unique label identifying the path.
   * @param id unique label identifying the path
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex);

  // - Pixels property storage -

  /**
   * For a particular Pixels, sets endianness of the pixels set.
   * @param bigEndian endianness of the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets the dimension order of the pixels set.
   * @param dimensionOrder the dimension order of the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets unique label identifying the pixels set.
   * @param id unique label identifying the pixels set
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsID(String id, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets the pixel type.
   * @param pixelType the pixel type
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets number of channels per timepoint.
   * @param sizeC number of channels per timepoint
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets number of timepoints.
   * @param sizeT number of timepoints
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets The size of an individual plane or section's X axis (width)..
   * @param sizeX The size of an individual plane or section's X axis (width).
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets The size of an individual plane or section's Y axis (height)..
   * @param sizeY The size of an individual plane or section's Y axis (height).
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, sets number of optical sections per stack.
   * @param sizeZ number of optical sections per stack
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex);

  // - Plane property storage -

  /**
   * For a particular Plane, sets the SHA1 hash of this plane's pixels.
   * @param hashSHA1 the SHA1 hash of this plane's pixels
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, sets unique label identifying this plane.
   * @param id unique label identifying this plane
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, sets the channel index.
   * @param theC the channel index
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, sets the timepoint.
   * @param theT the timepoint
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, sets the optical section index.
   * @param theZ the optical section index
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex);

  // - PlaneTiming property storage -

  /**
   * For a particular PlaneTiming, sets the time in seconds since the beginning of the experiment.
   * @param deltaT the time in seconds since the beginning of the experiment
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular PlaneTiming, sets the exposure time in seconds.
   * @param exposureTime the exposure time in seconds
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex);

  // - Plate property storage -

  /**
   * For a particular Plate, sets name of the first column in the plate.
   * @param columnNamingConvention name of the first column in the plate
   * @param plateIndex index of the Plate
   */
  void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex);

  /**
   * For a particular Plate, sets description of the plate.
   * @param description description of the plate
   * @param plateIndex index of the Plate
   */
  void setPlateDescription(String description, int plateIndex);

  /**
   * For a particular Plate, sets reference to the plate in an external database.
   * @param externalIdentifier reference to the plate in an external database
   * @param plateIndex index of the Plate
   */
  void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);

  /**
   * For a particular Plate, sets unique label identifying the plate.
   * @param id unique label identifying the plate
   * @param plateIndex index of the Plate
   */
  void setPlateID(String id, int plateIndex);

  /**
   * For a particular Plate, sets the plate's name.
   * @param name the plate's name
   * @param plateIndex index of the Plate
   */
  void setPlateName(String name, int plateIndex);

  /**
   * For a particular Plate, sets name of the first row in the plate.
   * @param rowNamingConvention name of the first row in the plate
   * @param plateIndex index of the Plate
   */
  void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex);

  /**
   * For a particular Plate, sets current state of the plate with respect to the experiment work-flow.
   * @param status current state of the plate with respect to the experiment work-flow
   * @param plateIndex index of the Plate
   */
  void setPlateStatus(String status, int plateIndex);

  /**
   * For a particular Plate, sets percent width offset from top left corner.
   * @param wellOriginX percent width offset from top left corner
   * @param plateIndex index of the Plate
   */
  void setPlateWellOriginX(Double wellOriginX, int plateIndex);

  /**
   * For a particular Plate, sets percent height offset from top left corner.
   * @param wellOriginY percent height offset from top left corner
   * @param plateIndex index of the Plate
   */
  void setPlateWellOriginY(Double wellOriginY, int plateIndex);

  // - PlateRef property storage -

  /**
   * For a particular PlateRef, sets TODO.
   * @param id TODO
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  void setPlateRefID(String id, int screenIndex, int plateRefIndex);

  /**
   * For a particular PlateRef, sets TODO.
   * @param sample TODO
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  void setPlateRefSample(Integer sample, int screenIndex, int plateRefIndex);

  /**
   * For a particular PlateRef, sets TODO.
   * @param well TODO
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  void setPlateRefWell(String well, int screenIndex, int plateRefIndex);

  // - Point property storage -

  /**
   * For a particular Point, sets X coordinate of the center of the point.
   * @param cx X coordinate of the center of the point
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, sets Y coordinate of the center of the point.
   * @param cy Y coordinate of the center of the point
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, sets unique label identifying the point.
   * @param id unique label identifying the point
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, sets radius of the point, in pixels.
   * @param r radius of the point, in pixels
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  // - Polygon property storage -

  /**
   * For a particular Polygon, sets unique label identifying the polygon.
   * @param id unique label identifying the polygon
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polygon, sets TODO.
   * @param points TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polygon, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  // - Polyline property storage -

  /**
   * For a particular Polyline, sets unique label identifying the polyline.
   * @param id unique label identifying the polyline
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polyline, sets TODO.
   * @param points TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polyline, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  // - Project property storage -

  /**
   * For a particular Project, sets description of the project.
   * @param description description of the project
   * @param projectIndex index of the Project
   */
  void setProjectDescription(String description, int projectIndex);

  /**
   * For a particular Project, sets reference to the experimenter who owns the project.
   * @param experimenterRef reference to the experimenter who owns the project
   * @param projectIndex index of the Project
   */
  void setProjectExperimenterRef(String experimenterRef, int projectIndex);

  /**
   * For a particular Project, sets reference to the group that owns the project.
   * @param groupRef reference to the group that owns the project
   * @param projectIndex index of the Project
   */
  void setProjectGroupRef(String groupRef, int projectIndex);

  /**
   * For a particular Project, sets unique label identifying the project.
   * @param id unique label identifying the project
   * @param projectIndex index of the Project
   */
  void setProjectID(String id, int projectIndex);

  /**
   * For a particular Project, sets name of the project.
   * @param name name of the project
   * @param projectIndex index of the Project
   */
  void setProjectName(String name, int projectIndex);

  // - ProjectRef property storage -

  /**
   * For a particular ProjectRef, sets unique label identifying the project.
   * @param id unique label identifying the project
   * @param datasetIndex index of the Dataset
   * @param projectRefIndex index of the ProjectRef
   */
  void setProjectRefID(String id, int datasetIndex, int projectRefIndex);

  // - Pump property storage -

  /**
   * For a particular Pump, sets unique label identifying the light source to be used as a pump.
   * @param lightSource unique label identifying the light source to be used as a pump
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex);

  // - ROI property storage -

  /**
   * For a particular ROI, sets unique label identifying the 5D bounding box ROI.
   * @param id unique label identifying the 5D bounding box ROI
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIID(String id, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting timepoint.
   * @param t0 the starting timepoint
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIT0(Integer t0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending timepoint.
   * @param t1 the ending timepoint
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIT1(Integer t1, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting X coordinate.
   * @param x0 the starting X coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIX0(Integer x0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending X coordinate.
   * @param x1 the ending X coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIX1(Integer x1, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting Y coordinate.
   * @param y0 the starting Y coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIY0(Integer y0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending Y coordinate.
   * @param y1 the ending Y coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIY1(Integer y1, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the starting Z coordinate.
   * @param z0 the starting Z coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIZ0(Integer z0, int imageIndex, int roiIndex);

  /**
   * For a particular ROI, sets the ending Z coordinate.
   * @param z1 the ending Z coordinate
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  void setROIZ1(Integer z1, int imageIndex, int roiIndex);

  // - ROIRef property storage -

  /**
   * For a particular ROIRef, sets reference to an ROI associated with the microbeam manipulation.
   * @param id reference to an ROI associated with the microbeam manipulation
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param roiRefIndex index of the ROIRef
   */
  void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex);

  // - Reagent property storage -

  /**
   * For a particular Reagent, sets the reagent's description.
   * @param description the reagent's description
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentDescription(String description, int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, sets unique label identifying the reagent.
   * @param id unique label identifying the reagent
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentID(String id, int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, sets the reagent's name.
   * @param name the reagent's name
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentName(String name, int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, sets reference to the reagent in an external database.
   * @param reagentIdentifier reference to the reagent in an external database
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

  // - Rect property storage -

  /**
   * For a particular Rect, sets height of the rectangle, in pixels.
   * @param height height of the rectangle, in pixels
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, sets unique label identifying the rectangle.
   * @param id unique label identifying the rectangle
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, sets TODO.
   * @param transform TODO
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, sets width of the rectangle, in pixels.
   * @param width width of the rectangle, in pixels
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, sets X coordinate of the upper left corner of the rectangle.
   * @param x X coordinate of the upper left corner of the rectangle
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, sets Y coordinate of the upper left corner of the rectangle.
   * @param y Y coordinate of the upper left corner of the rectangle
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex);

  // - Region property storage -

  /**
   * For a particular Region, sets TODO.
   * @param id TODO
   * @param imageIndex index of the Image
   * @param regionIndex index of the Region
   */
  void setRegionID(String id, int imageIndex, int regionIndex);

  /**
   * For a particular Region, sets TODO.
   * @param name TODO
   * @param imageIndex index of the Image
   * @param regionIndex index of the Region
   */
  void setRegionName(String name, int imageIndex, int regionIndex);

  /**
   * For a particular Region, sets TODO.
   * @param tag TODO
   * @param imageIndex index of the Image
   * @param regionIndex index of the Region
   */
  void setRegionTag(String tag, int imageIndex, int regionIndex);

  // - Screen property storage -

  /**
   * For a particular Screen, sets description of the screen.
   * @param description description of the screen
   * @param screenIndex index of the Screen
   */
  void setScreenDescription(String description, int screenIndex);

  /**
   * For a particular Screen, sets TODO.
   * @param extern TODO
   * @param screenIndex index of the Screen
   */
  void setScreenExtern(String extern, int screenIndex);

  /**
   * For a particular Screen, sets unique label identifying the screen.
   * @param id unique label identifying the screen
   * @param screenIndex index of the Screen
   */
  void setScreenID(String id, int screenIndex);

  /**
   * For a particular Screen, sets the screen's name.
   * @param name the screen's name
   * @param screenIndex index of the Screen
   */
  void setScreenName(String name, int screenIndex);

  /**
   * For a particular Screen, sets description of the screen's protocol.
   * @param protocolDescription description of the screen's protocol
   * @param screenIndex index of the Screen
   */
  void setScreenProtocolDescription(String protocolDescription, int screenIndex);

  /**
   * For a particular Screen, sets reference to an externally defined protocol.
   * @param protocolIdentifier reference to an externally defined protocol
   * @param screenIndex index of the Screen
   */
  void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);

  /**
   * For a particular Screen, sets description of a set of reagents.
   * @param reagentSetDescription description of a set of reagents
   * @param screenIndex index of the Screen
   */
  void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

  /**
   * For a particular Screen, sets reference to an externally defined set of reagents.
   * @param reagentSetIdentifier reference to an externally defined set of reagents
   * @param screenIndex index of the Screen
   */
  void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex);

  /**
   * For a particular Screen, sets human-readable screen type, e.g. RNAi, cDNA.
   * @param type human-readable screen type, e.g. RNAi, cDNA
   * @param screenIndex index of the Screen
   */
  void setScreenType(String type, int screenIndex);

  // - ScreenAcquisition property storage -

  /**
   * For a particular ScreenAcquisition, sets time when the last image was acquired.
   * @param endTime time when the last image was acquired
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, sets unique label identifying the screen's acquisition run.
   * @param id unique label identifying the screen's acquisition run
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, sets time when the first image was acquired.
   * @param startTime time when the first image was acquired
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex);

  // - ScreenRef property storage -

  /**
   * For a particular ScreenRef, sets label reference to a screen.
   * @param id label reference to a screen
   * @param plateIndex index of the Plate
   * @param screenRefIndex index of the ScreenRef
   */
  void setScreenRefID(String id, int plateIndex, int screenRefIndex);

  // - Shape property storage -

  /**
   * For a particular Shape, sets vertical text shift, e.g. sup, sub, normal, -70%. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param baselineShift vertical text shift, e.g. sup, sub, normal, -70%. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets text direction, e.g. ltr. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param direction text direction, e.g. ltr. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets name of the color with which to fill this ROI. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fillColor name of the color with which to fill this ROI. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets opacity (0-100) of the fill color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fillOpacity opacity (0-100) of the fill color. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets rule for filling the ROI, e.g. even-odd, non-zero. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fillRule rule for filling the ROI, e.g. even-odd, non-zero. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets the name of the font (e.g. "Arial"). This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fontFamily the name of the font (e.g. "Arial"). This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets size of the font. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fontSize size of the font. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFontSize(Integer fontSize, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets how the font should be stretched, e.g. normal, wider -  EXPERIMENTAL.
   * @param fontStretch how the font should be stretched, e.g. normal, wider -  EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets font style, e.g. normal, italic. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fontStyle font style, e.g. normal, italic. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets font variant, e.g. normal, small-caps. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fontVariant font variant, e.g. normal, small-caps. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets font weight, e.g. normal, bold. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param fontWeight font weight, e.g. normal, bold. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets group identifier. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param g group identifier. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param glyphOrientationVertical EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeGlyphOrientationVertical(Integer glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets unique label identifying the shape.
   * @param id unique label identifying the shape
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets whether or not the ROI can be modified. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param locked whether or not the ROI can be modified. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeLocked(Boolean locked, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets additional stroke information. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param strokeAttribute additional stroke information. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets name of the stroke color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param strokeColor name of the stroke color. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param strokeDashArray EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param strokeLineCap EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param strokeLineJoin EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param strokeMiterLimit EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeMiterLimit(Integer strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets opacity (0-100) of the stroke color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param strokeOpacity opacity (0-100) of the stroke color. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeOpacity(Float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets width (in pixels) of the stroke. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param strokeWidth width (in pixels) of the stroke. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeStrokeWidth(Integer strokeWidth, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets text label associated with this shape. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param text text label associated with this shape. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets relative position of the text, e.g. start, middle. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param textAnchor relative position of the text, e.g. start, middle. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets text decoration, e.g. underline, line-through. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param textDecoration text decoration, e.g. underline, line-through. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets text color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param textFill text color. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param textStroke EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets T position of the shape.
   * @param theT T position of the shape
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeTheT(Integer theT, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets Z position of the shape.
   * @param theZ Z position of the shape
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeTheZ(Integer theZ, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param vectorEffect EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets whether or not the ROI should be displayed. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param visibility whether or not the ROI should be displayed. This attribute is experimental, and not present in the existing OME-XML schemas.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeVisibility(Boolean visibility, int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, sets EXPERIMENTAL.
   * @param writingMode EXPERIMENTAL
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex);

  // - StageLabel property storage -

  /**
   * For a particular StageLabel, sets a name for the stage label.
   * @param name a name for the stage label
   * @param imageIndex index of the Image
   */
  void setStageLabelName(String name, int imageIndex);

  /**
   * For a particular StageLabel, sets the x coordinate of the stage.
   * @param x the x coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelX(Float x, int imageIndex);

  /**
   * For a particular StageLabel, sets the y coordinate of the stage.
   * @param y the y coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelY(Float y, int imageIndex);

  /**
   * For a particular StageLabel, sets the z coordinate of the stage.
   * @param z the z coordinate of the stage
   * @param imageIndex index of the Image
   */
  void setStageLabelZ(Float z, int imageIndex);

  // - StagePosition property storage -

  /**
   * For a particular StagePosition, sets the X coordinate of the stage position.
   * @param positionX the X coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, sets the Y coordinate of the stage position.
   * @param positionY the Y coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, sets the Z coordinate of the stage position.
   * @param positionZ the Z coordinate of the stage position
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex);

  // - Thumbnail property storage -

  /**
   * For a particular Thumbnail, sets external URI referring to the thumbnail.
   * @param href external URI referring to the thumbnail
   * @param imageIndex index of the Image
   */
  void setThumbnailHref(String href, int imageIndex);

  /**
   * For a particular Thumbnail, sets unique label identifying the thumbnail.
   * @param id unique label identifying the thumbnail
   * @param imageIndex index of the Image
   */
  void setThumbnailID(String id, int imageIndex);

  /**
   * For a particular Thumbnail, sets MIME-type of the thumbnail; must be set to 'SVG' if thumbnail data is embedded.
   * @param mimEtype MIME-type of the thumbnail; must be set to 'SVG' if thumbnail data is embedded
   * @param imageIndex index of the Image
   */
  void setThumbnailMIMEtype(String mimEtype, int imageIndex);

  // - TiffData property storage -

  /**
   * For a particular TiffData, sets the name of the file containing these IFDs.
   * @param fileName the name of the file containing these IFDs
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets C position of the image plane at the specified IFD, indexed from 0.
   * @param firstC C position of the image plane at the specified IFD, indexed from 0
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets T position of the image plane at the specified IFD, indexed from 0.
   * @param firstT T position of the image plane at the specified IFD, indexed from 0
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets Z position of the image plane at the specified IFD, indexed from 0.
   * @param firstZ Z position of the image plane at the specified IFD, indexed from 0
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets first IFD for which this element is applicable, indexed from 0.
   * @param ifd first IFD for which this element is applicable, indexed from 0
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets Number of IFDs for which this element is applicable; default is the number of planes (if no IFD is specified), or 1 (if an IFD is specified)..
   * @param numPlanes Number of IFDs for which this element is applicable; default is the number of planes (if no IFD is specified), or 1 (if an IFD is specified).
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, sets unique identifier indicating which file contains these IFDs.
   * @param uuid unique identifier indicating which file contains these IFDs
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex);

  // - TransmittanceRange property storage -

  /**
   * For a particular TransmittanceRange, sets TODO.
   * @param cutIn TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setTransmittanceRangeCutIn(Integer cutIn, int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, sets TODO.
   * @param cutInTolerance TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setTransmittanceRangeCutInTolerance(Integer cutInTolerance, int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, sets TODO.
   * @param cutOut TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setTransmittanceRangeCutOut(Integer cutOut, int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, sets TODO.
   * @param cutOutTolerance TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setTransmittanceRangeCutOutTolerance(Integer cutOutTolerance, int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, sets TODO.
   * @param transmittance TODO
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  void setTransmittanceRangeTransmittance(Integer transmittance, int instrumentIndex, int filterIndex);

  // - Well property storage -

  /**
   * For a particular Well, sets column index of the well, where top-left is 0.
   * @param column column index of the well, where top-left is 0
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellColumn(Integer column, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets description of the externally defined ID for the well.
   * @param externalDescription description of the externally defined ID for the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets reference to the well in an external database.
   * @param externalIdentifier reference to the well in an external database
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets unique label identifying the well.
   * @param id unique label identifying the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellID(String id, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets label reference for the associated reagent.
   * @param reagent label reference for the associated reagent
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellReagent(String reagent, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets row index of the well, where top-left is 0.
   * @param row row index of the well, where top-left is 0
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellRow(Integer row, int plateIndex, int wellIndex);

  /**
   * For a particular Well, sets human-readable identifier of the screen status, e.g. empty, control.
   * @param type human-readable identifier of the screen status, e.g. empty, control
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  void setWellType(String type, int plateIndex, int wellIndex);

  // - WellSample property storage -

  /**
   * For a particular WellSample, sets unique label identifying the individual well image.
   * @param id unique label identifying the individual well image
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets label reference for the associated image.
   * @param imageRef label reference for the associated image
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets link to the Image element.
   * @param index link to the Image element
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets X position of the image within the well.
   * @param posX X position of the image within the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets Y position of the image within the well.
   * @param posY Y position of the image within the well
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, sets time-point at which the image started to be collected.
   * @param timepoint time-point at which the image started to be collected
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

  // - WellSampleRef property storage -

  /**
   * For a particular WellSampleRef, sets TODO.
   * @param id TODO
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   * @param wellSampleRefIndex index of the WellSampleRef
   */
  void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);

}
