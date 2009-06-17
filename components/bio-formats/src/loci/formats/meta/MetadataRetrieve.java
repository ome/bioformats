//
// MetadataRetrieve.java
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
 * Created by melissa via MetadataAutogen on Jun 17, 2009 12:24:44 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

/**
 * A proxy whose responsibility it is to extract biological image data from a
 * particular storage medium.
 *
 * <p>The <code>MetadataRetrieve</code> interface encompasses the metadata
 * that any specific storage medium (file, relational database, etc.) should be
 * expected to access from its backing data model.
 *
 * <p>The <code>MetadataRetrieve</code> interface goes hand in hand with the
 * <code>MetadataStore</code> interface. Essentially,
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
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/MetadataRetrieve.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/MetadataRetrieve.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataRetrieve {

  // - Entity counting -

  int getChannelComponentCount(int imageIndex, int logicalChannelIndex);

  int getDatasetCount();

  int getDatasetRefCount(int imageIndex);

  int getDetectorCount(int instrumentIndex);

  int getDichroicCount(int instrumentIndex);

  int getExperimentCount();

  int getExperimenterCount();

  int getExperimenterMembershipCount(int experimenterIndex);

  int getFilterCount(int instrumentIndex);

  int getFilterSetCount(int instrumentIndex);

  int getGroupCount();

  int getGroupRefCount(int experimenterIndex);

  int getImageCount();

  int getInstrumentCount();

  int getLightSourceCount(int instrumentIndex);

  int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex);

  int getLogicalChannelCount(int imageIndex);

  int getMicrobeamManipulationCount(int imageIndex);

  int getMicrobeamManipulationRefCount(int experimentIndex);

  int getOTFCount(int instrumentIndex);

  int getObjectiveCount(int instrumentIndex);

  int getPixelsCount(int imageIndex);

  int getPlaneCount(int imageIndex, int pixelsIndex);

  int getPlateCount();

  int getPlateRefCount(int screenIndex);

  int getProjectCount();

  int getProjectRefCount(int datasetIndex);

  int getROICount(int imageIndex);

  int getROIRefCount(int imageIndex, int microbeamManipulationIndex);

  int getReagentCount(int screenIndex);

  int getRegionCount(int imageIndex);

  int getScreenCount();

  int getScreenAcquisitionCount(int screenIndex);

  int getScreenRefCount(int plateIndex);

  int getShapeCount(int imageIndex, int roiIndex);

  int getTiffDataCount(int imageIndex, int pixelsIndex);

  int getWellCount(int plateIndex);

  int getWellSampleCount(int plateIndex, int wellIndex);

  int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex);

  // - Entity retrieval -

  /** Gets the UUID associated with this collection of metadata. */
  String getUUID();

  // - Arc property retrieval -

  /**
   * For a particular Arc, gets the type of arc.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getArcType(int instrumentIndex, int lightSourceIndex);

  // - ChannelComponent property retrieval -

  /**
   * For a particular ChannelComponent, gets which color channel this ChannelComponent belongs to (for example, 'R' for an 'RGB' PhotometricInterpretation).
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  /**
   * For a particular ChannelComponent, gets the index into the channel dimension of the 5-D pixel array.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  Integer getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  /**
   * For a particular ChannelComponent, gets the pixels to which this channel component corresponds.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   * @param channelComponentIndex index of the ChannelComponent
   */
  String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex);

  // - Circle property retrieval -

  /**
   * For a particular Circle, gets X coordinate of the center of the circle.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getCircleCx(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, gets Y coordinate of the center of the circle.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getCircleCy(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, gets unique label identifying the circle.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getCircleID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, gets radius of the circle, in pixels.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getCircleR(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Circle, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex);

  // - Contact property retrieval -

  /**
   * For a particular Contact, gets unique label identifying the experimenter who is the group contact.
   * @param groupIndex index of the Group
   */
  String getContactExperimenter(int groupIndex);

  // - Dataset property retrieval -

  /**
   * For a particular Dataset, gets description of the dataset.
   * @param datasetIndex index of the Dataset
   */
  String getDatasetDescription(int datasetIndex);

  /**
   * For a particular Dataset, gets reference to the experimenter who owns the dataset.
   * @param datasetIndex index of the Dataset
   */
  String getDatasetExperimenterRef(int datasetIndex);

  /**
   * For a particular Dataset, gets reference to the group that owns the dataset.
   * @param datasetIndex index of the Dataset
   */
  String getDatasetGroupRef(int datasetIndex);

  /**
   * For a particular Dataset, gets unique label identifying the dataset.
   * @param datasetIndex index of the Dataset
   */
  String getDatasetID(int datasetIndex);

  /**
   * For a particular Dataset, gets indicates whether the images in the dataset can be altered.
   * @param datasetIndex index of the Dataset
   */
  Boolean getDatasetLocked(int datasetIndex);

  /**
   * For a particular Dataset, gets name of the dataset.
   * @param datasetIndex index of the Dataset
   */
  String getDatasetName(int datasetIndex);

  // - DatasetRef property retrieval -

  /**
   * For a particular DatasetRef, gets TODO.
   * @param imageIndex index of the Image
   * @param datasetRefIndex index of the DatasetRef
   */
  String getDatasetRefID(int imageIndex, int datasetRefIndex);

  // - Detector property retrieval -

  /**
   * For a particular Detector, gets amplification gain of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets the gain of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorGain(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets unique label identifying the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorID(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets name of the detector manufacturer.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets model name/number of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorModel(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets the offset of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorOffset(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets serial number of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets the detector type, e.g. CCD, PMT.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  String getDetectorType(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets the voltage of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorVoltage(int instrumentIndex, int detectorIndex);

  /**
   * For a particular Detector, gets optional zoom value of the detector.
   * @param instrumentIndex index of the Instrument
   * @param detectorIndex index of the Detector
   */
  Float getDetectorZoom(int instrumentIndex, int detectorIndex);

  // - DetectorSettings property retrieval -

  /**
   * For a particular DetectorSettings, gets the detector binning.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the detector associated with this channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the detector gain.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the detector offset.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the speed at which the detector can count pixels, in MHz.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular DetectorSettings, gets the detector voltage.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex);

  // - Dichroic property retrieval -

  /**
   * For a particular Dichroic, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param dichroicIndex index of the Dichroic
   */
  String getDichroicLotNumber(int instrumentIndex, int dichroicIndex);

  /**
   * For a particular Dichroic, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param dichroicIndex index of the Dichroic
   */
  String getDichroicManufacturer(int instrumentIndex, int dichroicIndex);

  /**
   * For a particular Dichroic, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param dichroicIndex index of the Dichroic
   */
  String getDichroicModel(int instrumentIndex, int dichroicIndex);

  // - Dimensions property retrieval -

  /**
   * For a particular Dimensions, gets the size of an individual pixel's X axis in microns.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the size of an individual pixel's Y axis in microns.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the size of an individual pixel's Z axis in microns.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the distance between adjacent time points in seconds.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the distance between adjacent wavelengths in nanometers.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex);

  /**
   * For a particular Dimensions, gets the starting wavelength in nanometers.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex);

  // - DisplayOptions property retrieval -

  /**
   * For a particular DisplayOptions, gets specifies to display the image as grayscale or RGB.
   * @param imageIndex index of the Image
   */
  String getDisplayOptionsDisplay(int imageIndex);

  /**
   * For a particular DisplayOptions, gets unique label identifying the display options.
   * @param imageIndex index of the Image
   */
  String getDisplayOptionsID(int imageIndex);

  /**
   * For a particular DisplayOptions, gets zoom factor for use in the display (NOT THE LENS ZOOM).
   * @param imageIndex index of the Image
   */
  Float getDisplayOptionsZoom(int imageIndex);

  // - DisplayOptionsProjection property retrieval -

  /**
   * For a particular DisplayOptionsProjection, gets the first focal plane to include in the maximum intensity projection.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsProjectionZStart(int imageIndex);

  /**
   * For a particular DisplayOptionsProjection, gets the last focal plane to include in the maximum intensity projection.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsProjectionZStop(int imageIndex);

  // - DisplayOptionsTime property retrieval -

  /**
   * For a particular DisplayOptionsTime, gets the first time point to include in the animation.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsTimeTStart(int imageIndex);

  /**
   * For a particular DisplayOptionsTime, gets the last time point to include in the animation.
   * @param imageIndex index of the Image
   */
  Integer getDisplayOptionsTimeTStop(int imageIndex);

  // - Ellipse property retrieval -

  /**
   * For a particular Ellipse, gets X coordinate of the center of the ellipse.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, gets Y coordinate of the center of the ellipse.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, gets unique label identifying the ellipse.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getEllipseID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, gets horizontal radius of the ellipse.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, gets vertical radius of the ellipse.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Ellipse, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex);

  // - EmFilter property retrieval -

  /**
   * For a particular EmFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getEmFilterLotNumber(int instrumentIndex, int filterIndex);

  /**
   * For a particular EmFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getEmFilterManufacturer(int instrumentIndex, int filterIndex);

  /**
   * For a particular EmFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getEmFilterModel(int instrumentIndex, int filterIndex);

  /**
   * For a particular EmFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getEmFilterType(int instrumentIndex, int filterIndex);

  // - ExFilter property retrieval -

  /**
   * For a particular ExFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getExFilterLotNumber(int instrumentIndex, int filterIndex);

  /**
   * For a particular ExFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getExFilterManufacturer(int instrumentIndex, int filterIndex);

  /**
   * For a particular ExFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getExFilterModel(int instrumentIndex, int filterIndex);

  /**
   * For a particular ExFilter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getExFilterType(int instrumentIndex, int filterIndex);

  // - Experiment property retrieval -

  /**
   * For a particular Experiment, gets description of the experiment.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentDescription(int experimentIndex);

  /**
   * For a particular Experiment, gets ID of the experimenter who conducted the experiment.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentExperimenterRef(int experimentIndex);

  /**
   * For a particular Experiment, gets unique label identifying the experiment.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentID(int experimentIndex);

  /**
   * For a particular Experiment, gets type of experiment, e.g. FRET, TimeLapse.
   * @param experimentIndex index of the Experiment
   */
  String getExperimentType(int experimentIndex);

  // - Experimenter property retrieval -

  /**
   * For a particular Experimenter, gets the e-mail address of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterEmail(int experimenterIndex);

  /**
   * For a particular Experimenter, gets the first name of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterFirstName(int experimenterIndex);

  /**
   * For a particular Experimenter, gets unique label identifying the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterID(int experimenterIndex);

  /**
   * For a particular Experimenter, gets the institution to which the experimenter belongs.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterInstitution(int experimenterIndex);

  /**
   * For a particular Experimenter, gets the last name of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterLastName(int experimenterIndex);

  /**
   * For a particular Experimenter, gets OME user name of the experimenter.
   * @param experimenterIndex index of the Experimenter
   */
  String getExperimenterOMEName(int experimenterIndex);

  // - ExperimenterMembership property retrieval -

  /**
   * For a particular ExperimenterMembership, gets the group associated with this membership.
   * @param experimenterIndex index of the Experimenter
   * @param groupRefIndex index of the GroupRef
   */
  String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex);

  // - Filament property retrieval -

  /**
   * For a particular Filament, gets the type of filament.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getFilamentType(int instrumentIndex, int lightSourceIndex);

  // - Filter property retrieval -

  /**
   * For a particular Filter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getFilterFilterWheel(int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, gets unique label identifying the filter.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getFilterID(int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getFilterLotNumber(int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getFilterManufacturer(int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getFilterModel(int instrumentIndex, int filterIndex);

  /**
   * For a particular Filter, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  String getFilterType(int instrumentIndex, int filterIndex);

  // - FilterSet property retrieval -

  /**
   * For a particular FilterSet, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  String getFilterSetDichroic(int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  String getFilterSetExFilter(int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);

  /**
   * For a particular FilterSet, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterSetIndex index of the FilterSet
   */
  String getFilterSetModel(int instrumentIndex, int filterSetIndex);

  // - GreyChannel property retrieval -

  /**
   * For a particular GreyChannel, gets dimmest pixel value; anything below this will be set to 0.
   * @param imageIndex index of the Image
   */
  Float getGreyChannelBlackLevel(int imageIndex);

  /**
   * For a particular GreyChannel, gets actual channel number, indexed from 0.
   * @param imageIndex index of the Image
   */
  Integer getGreyChannelChannelNumber(int imageIndex);

  /**
   * For a particular GreyChannel, gets gamma value for this channel.
   * @param imageIndex index of the Image
   */
  Float getGreyChannelGamma(int imageIndex);

  /**
   * For a particular GreyChannel, gets brightest pixel value; anything above this will be set to 255.
   * @param imageIndex index of the Image
   */
  Float getGreyChannelWhiteLevel(int imageIndex);

  /**
   * For a particular GreyChannel, gets TODO.
   * @param imageIndex index of the Image
   */
  Boolean getGreyChannelisOn(int imageIndex);

  // - GreyChannelMap property retrieval -

  /**
   * For a particular GreyChannelMap, gets describes color mapping function - Greyscale, Spectrum, or Blackbody.
   * @param imageIndex index of the Image
   */
  String getGreyChannelMapColorMap(int imageIndex);

  // - Group property retrieval -

  /**
   * For a particular Group, gets the name of the group.
   * @param groupIndex index of the Group
   */
  String getGroupName(int groupIndex);

  // - GroupRef property retrieval -

  // - Image property retrieval -

  /**
   * For a particular Image, gets acquired pixels set for the image.
   * @param imageIndex index of the Image
   */
  String getImageAcquiredPixels(int imageIndex);

  /**
   * For a particular Image, gets the creation date of the image.
   * @param imageIndex index of the Image
   */
  String getImageCreationDate(int imageIndex);

  /**
   * For a particular Image, gets default pixels set for the image.
   * @param imageIndex index of the Image
   */
  String getImageDefaultPixels(int imageIndex);

  /**
   * For a particular Image, gets the full description of the image.
   * @param imageIndex index of the Image
   */
  String getImageDescription(int imageIndex);

  /**
   * For a particular Image, gets reference to the experiment to which this image belongs.
   * @param imageIndex index of the Image
   */
  String getImageExperimentRef(int imageIndex);

  /**
   * For a particular Image, gets reference to the experimenter who created this image.
   * @param imageIndex index of the Image
   */
  String getImageExperimenterRef(int imageIndex);

  /**
   * For a particular Image, gets reference to the group that owns this image.
   * @param imageIndex index of the Image
   */
  String getImageGroupRef(int imageIndex);

  /**
   * For a particular Image, gets unique label identifying the image.
   * @param imageIndex index of the Image
   */
  String getImageID(int imageIndex);

  /**
   * For a particular Image, gets label reference for the associated instrument.
   * @param imageIndex index of the Image
   */
  String getImageInstrumentRef(int imageIndex);

  /**
   * For a particular Image, gets the full name of the image.
   * @param imageIndex index of the Image
   */
  String getImageName(int imageIndex);

  /**
   * For a particular Image, gets reference to the objective used when acquiring this image.
   * @param imageIndex index of the Image
   */
  String getImageObjective(int imageIndex);

  // - ImagingEnvironment property retrieval -

  /**
   * For a particular ImagingEnvironment, gets air pressure, in millibars.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentAirPressure(int imageIndex);

  /**
   * For a particular ImagingEnvironment, gets CO2 level, in percent fractions from 0.0 to 1.0.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentCO2Percent(int imageIndex);

  /**
   * For a particular ImagingEnvironment, gets humidity, in percent fractions from 0.0 to 1.0.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentHumidity(int imageIndex);

  /**
   * For a particular ImagingEnvironment, gets temperature of the imaging environment, in Celsius.
   * @param imageIndex index of the Image
   */
  Float getImagingEnvironmentTemperature(int imageIndex);

  // - Instrument property retrieval -

  /**
   * For a particular Instrument, gets unique label identifying the instrument.
   * @param instrumentIndex index of the Instrument
   */
  String getInstrumentID(int instrumentIndex);

  // - Laser property retrieval -

  /**
   * For a particular Laser, gets value by which frequency is multiplied.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets lasing medium for the laser.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserPulse(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets rate in Hz at which the laser pulses.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets indicates whether or not the laser can be tuned.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets type of laser - Excimer, Gas, MetalVapor, SolidState, Dye, SemiConductor, FreeElectron, or Unknown.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLaserType(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular Laser, gets wavelength of the laser, in nm.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex);

  // - LightSource property retrieval -

  /**
   * For a particular LightSource, gets unique label identifying the light source.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceID(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets manufacturer of the light source.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets model number of the light source.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceModel(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets the light source power, in watts.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  Float getLightSourcePower(int instrumentIndex, int lightSourceIndex);

  /**
   * For a particular LightSource, gets serial number of the light source.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

  // - LightSourceRef property retrieval -

  /**
   * For a particular LightSourceRef, gets the primary light source attenuation.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param lightSourceRefIndex index of the LightSourceRef
   */
  Float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

  /**
   * For a particular LightSourceRef, gets TODO.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param lightSourceRefIndex index of the LightSourceRef
   */
  String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

  /**
   * For a particular LightSourceRef, gets the primary light source wavelength.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param lightSourceRefIndex index of the LightSourceRef
   */
  Integer getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex);

  // - LightSourceSettings property retrieval -

  /**
   * For a particular LightSourceSettings, gets the primary light source attenuation.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, gets the primary light source.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LightSourceSettings, gets the primary light source wavelength.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex);

  // - Line property retrieval -

  /**
   * For a particular Line, gets unique label identifying the line.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getLineID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getLineTransform(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, gets X coordinate of the first endpoint of the line.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getLineX1(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, gets X coordinate of the second endpoint of the line.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getLineX2(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, gets Y coordinate of the first endpoint of the line.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getLineY1(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Line, gets Y coordinate of the second endpoint of the line.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getLineY2(int imageIndex, int roiIndex, int shapeIndex);

  // - LogicalChannel property retrieval -

  /**
   * For a particular LogicalChannel, gets the constrast method name.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets reference to the detector used to acquire this logical channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the emission wavelength.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the excitation wavelength.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets reference to the filter set associated with this channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the fluorescence type.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets unique label identifying the logical channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelID(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the illumination type.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets reference to the light source used to acquire this logical channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the acquisition mode.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelMode(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the logical channel's name.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelName(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the neutral-density filter value.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the OTF associated with the logical channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the photometric interpretation type.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets the size of the pinhole.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets TODO.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets number of channel components in the logical channel.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets TODO.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex);

  /**
   * For a particular LogicalChannel, gets TODO.
   * @param imageIndex index of the Image
   * @param logicalChannelIndex index of the LogicalChannel
   */
  String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex);

  // - Mask property retrieval -

  /**
   * For a particular Mask, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, gets unique label identifying the mask.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskX(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Mask, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskY(int imageIndex, int roiIndex, int shapeIndex);

  // - MaskPixels property retrieval -

  /**
   * For a particular MaskPixels, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, gets unique label identifying the mask's pixels.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular MaskPixels, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex);

  // - MicrobeamManipulation property retrieval -

  /**
   * For a particular MicrobeamManipulation, gets reference to the experimenter who applied this manipulation.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   */
  String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex);

  /**
   * For a particular MicrobeamManipulation, gets unique label identifying the microbeam manipulation.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   */
  String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex);

  /**
   * For a particular MicrobeamManipulation, gets type of operation - FRAP, Photoablation, Photoactivation, Uncaging, OpticalTrapping, or Other.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   */
  String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex);

  // - MicrobeamManipulationRef property retrieval -

  /**
   * For a particular MicrobeamManipulationRef, gets TODO.
   * @param experimentIndex index of the Experiment
   * @param microbeamManipulationRefIndex index of the MicrobeamManipulationRef
   */
  String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex);

  // - Microscope property retrieval -

  /**
   * For a particular Microscope, gets unique label identifying the microscope.
   * @param instrumentIndex index of the Instrument
   */
  String getMicroscopeID(int instrumentIndex);

  /**
   * For a particular Microscope, gets manufacturer of the microscope.
   * @param instrumentIndex index of the Instrument
   */
  String getMicroscopeManufacturer(int instrumentIndex);

  /**
   * For a particular Microscope, gets model number of the microscope.
   * @param instrumentIndex index of the Instrument
   */
  String getMicroscopeModel(int instrumentIndex);

  /**
   * For a particular Microscope, gets serial number of the microscope.
   * @param instrumentIndex index of the Instrument
   */
  String getMicroscopeSerialNumber(int instrumentIndex);

  /**
   * For a particular Microscope, gets microscope type - Upright, Inverted, Dissection, Electrophysiology, or Unknown.
   * @param instrumentIndex index of the Instrument
   */
  String getMicroscopeType(int instrumentIndex);

  // - OTF property retrieval -

  /**
   * For a particular OTF, gets the Base64-encoded optical transfer function, or the path to a file containing it.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFBinaryFile(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets unique label identifying the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFID(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets objective described by the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFObjective(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets indicates whether or not optical axis averaging was performed.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets the pixel type of the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  String getOTFPixelType(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets width of the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Integer getOTFSizeX(int instrumentIndex, int otfIndex);

  /**
   * For a particular OTF, gets height of the optical transfer function.
   * @param instrumentIndex index of the Instrument
   * @param otfIndex index of the OTF
   */
  Integer getOTFSizeY(int instrumentIndex, int otfIndex);

  // - Objective property retrieval -

  /**
   * For a particular Objective, gets the measured magnification of the objective.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets type of correction coating applied to the objective lens.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets unique label identifying the objective.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveID(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets immersion medium used with the objective lens.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets numerical aperture of the lens.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets the name of the objective's manufacturer.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets model name/number of the objective.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveModel(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets the specified magnification of the objective.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets serial number of the objective.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

  /**
   * For a particular Objective, gets working distance of the objective, in um.
   * @param instrumentIndex index of the Instrument
   * @param objectiveIndex index of the Objective
   */
  Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

  // - ObjectiveSettings property retrieval -

  /**
   * For a particular ObjectiveSettings, gets unit-less setting of the adjustable correction collar.
   * @param imageIndex index of the Image
   */
  Float getObjectiveSettingsCorrectionCollar(int imageIndex);

  /**
   * For a particular ObjectiveSettings, gets immersion medium for the lens.
   * @param imageIndex index of the Image
   */
  String getObjectiveSettingsMedium(int imageIndex);

  /**
   * For a particular ObjectiveSettings, gets the objective associated with this image.
   * @param imageIndex index of the Image
   */
  String getObjectiveSettingsObjective(int imageIndex);

  /**
   * For a particular ObjectiveSettings, gets unit-less refractive index of the immersion medium.
   * @param imageIndex index of the Image
   */
  Float getObjectiveSettingsRefractiveIndex(int imageIndex);

  // - Path property retrieval -

  /**
   * For a particular Path, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPathD(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Path, gets unique label identifying the path.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPathID(int imageIndex, int roiIndex, int shapeIndex);

  // - Pixels property retrieval -

  /**
   * For a particular Pixels, gets endianness of the pixels set.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets the dimension order of the pixels set.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsDimensionOrder(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets unique label identifying the pixels set.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsID(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets the pixel type.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  String getPixelsPixelType(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets number of channels per timepoint.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeC(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets number of timepoints.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeT(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets The size of an individual plane or section's X axis (width)..
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeX(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets The size of an individual plane or section's Y axis (height)..
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeY(int imageIndex, int pixelsIndex);

  /**
   * For a particular Pixels, gets number of optical sections per stack.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   */
  Integer getPixelsSizeZ(int imageIndex, int pixelsIndex);

  // - Plane property retrieval -

  /**
   * For a particular Plane, gets the SHA1 hash of this plane's pixels.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, gets unique label identifying this plane.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, gets the channel index.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, gets the timepoint.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular Plane, gets the optical section index.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex);

  // - PlaneTiming property retrieval -

  /**
   * For a particular PlaneTiming, gets the time in seconds since the beginning of the experiment.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular PlaneTiming, gets the exposure time in seconds.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex);

  // - Plate property retrieval -

  /**
   * For a particular Plate, gets name of the first column in the plate.
   * @param plateIndex index of the Plate
   */
  String getPlateColumnNamingConvention(int plateIndex);

  /**
   * For a particular Plate, gets description of the plate.
   * @param plateIndex index of the Plate
   */
  String getPlateDescription(int plateIndex);

  /**
   * For a particular Plate, gets reference to the plate in an external database.
   * @param plateIndex index of the Plate
   */
  String getPlateExternalIdentifier(int plateIndex);

  /**
   * For a particular Plate, gets unique label identifying the plate.
   * @param plateIndex index of the Plate
   */
  String getPlateID(int plateIndex);

  /**
   * For a particular Plate, gets the plate's name.
   * @param plateIndex index of the Plate
   */
  String getPlateName(int plateIndex);

  /**
   * For a particular Plate, gets name of the first row in the plate.
   * @param plateIndex index of the Plate
   */
  String getPlateRowNamingConvention(int plateIndex);

  /**
   * For a particular Plate, gets current state of the plate with respect to the experiment work-flow.
   * @param plateIndex index of the Plate
   */
  String getPlateStatus(int plateIndex);

  /**
   * For a particular Plate, gets percent width offset from top left corner.
   * @param plateIndex index of the Plate
   */
  Double getPlateWellOriginX(int plateIndex);

  /**
   * For a particular Plate, gets percent height offset from top left corner.
   * @param plateIndex index of the Plate
   */
  Double getPlateWellOriginY(int plateIndex);

  // - PlateRef property retrieval -

  /**
   * For a particular PlateRef, gets TODO.
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  String getPlateRefID(int screenIndex, int plateRefIndex);

  /**
   * For a particular PlateRef, gets TODO.
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  Integer getPlateRefSample(int screenIndex, int plateRefIndex);

  /**
   * For a particular PlateRef, gets TODO.
   * @param screenIndex index of the Screen
   * @param plateRefIndex index of the PlateRef
   */
  String getPlateRefWell(int screenIndex, int plateRefIndex);

  // - Point property retrieval -

  /**
   * For a particular Point, gets X coordinate of the center of the point.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPointCx(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, gets Y coordinate of the center of the point.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPointCy(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, gets unique label identifying the point.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPointID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, gets radius of the point, in pixels.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPointR(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Point, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPointTransform(int imageIndex, int roiIndex, int shapeIndex);

  // - Polygon property retrieval -

  /**
   * For a particular Polygon, gets unique label identifying the polygon.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPolygonID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polygon, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polygon, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex);

  // - Polyline property retrieval -

  /**
   * For a particular Polyline, gets unique label identifying the polyline.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPolylineID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polyline, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Polyline, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex);

  // - Project property retrieval -

  /**
   * For a particular Project, gets description of the project.
   * @param projectIndex index of the Project
   */
  String getProjectDescription(int projectIndex);

  /**
   * For a particular Project, gets reference to the experimenter who owns the project.
   * @param projectIndex index of the Project
   */
  String getProjectExperimenterRef(int projectIndex);

  /**
   * For a particular Project, gets reference to the group that owns the project.
   * @param projectIndex index of the Project
   */
  String getProjectGroupRef(int projectIndex);

  /**
   * For a particular Project, gets unique label identifying the project.
   * @param projectIndex index of the Project
   */
  String getProjectID(int projectIndex);

  /**
   * For a particular Project, gets name of the project.
   * @param projectIndex index of the Project
   */
  String getProjectName(int projectIndex);

  // - ProjectRef property retrieval -

  /**
   * For a particular ProjectRef, gets unique label identifying the project.
   * @param datasetIndex index of the Dataset
   * @param projectRefIndex index of the ProjectRef
   */
  String getProjectRefID(int datasetIndex, int projectRefIndex);

  // - Pump property retrieval -

  /**
   * For a particular Pump, gets unique label identifying the light source to be used as a pump.
   * @param instrumentIndex index of the Instrument
   * @param lightSourceIndex index of the LightSource
   */
  String getPumpLightSource(int instrumentIndex, int lightSourceIndex);

  // - ROI property retrieval -

  /**
   * For a particular ROI, gets unique label identifying the 5D bounding box ROI.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  String getROIID(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting timepoint.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIT0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending timepoint.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIT1(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting X coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIX0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending X coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIX1(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting Y coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIY0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending Y coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIY1(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the starting Z coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIZ0(int imageIndex, int roiIndex);

  /**
   * For a particular ROI, gets the ending Z coordinate.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   */
  Integer getROIZ1(int imageIndex, int roiIndex);

  // - ROIRef property retrieval -

  /**
   * For a particular ROIRef, gets reference to an ROI associated with the microbeam manipulation.
   * @param imageIndex index of the Image
   * @param microbeamManipulationIndex index of the MicrobeamManipulation
   * @param roiRefIndex index of the ROIRef
   */
  String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex);

  // - Reagent property retrieval -

  /**
   * For a particular Reagent, gets the reagent's description.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentDescription(int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, gets unique label identifying the reagent.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentID(int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, gets the reagent's name.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentName(int screenIndex, int reagentIndex);

  /**
   * For a particular Reagent, gets reference to the reagent in an external database.
   * @param screenIndex index of the Screen
   * @param reagentIndex index of the Reagent
   */
  String getReagentReagentIdentifier(int screenIndex, int reagentIndex);

  // - Rect property retrieval -

  /**
   * For a particular Rect, gets height of the rectangle, in pixels.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getRectHeight(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, gets unique label identifying the rectangle.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getRectID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, gets TODO.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getRectTransform(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, gets width of the rectangle, in pixels.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getRectWidth(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, gets X coordinate of the upper left corner of the rectangle.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getRectX(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Rect, gets Y coordinate of the upper left corner of the rectangle.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getRectY(int imageIndex, int roiIndex, int shapeIndex);

  // - Region property retrieval -

  /**
   * For a particular Region, gets TODO.
   * @param imageIndex index of the Image
   * @param regionIndex index of the Region
   */
  String getRegionID(int imageIndex, int regionIndex);

  /**
   * For a particular Region, gets TODO.
   * @param imageIndex index of the Image
   * @param regionIndex index of the Region
   */
  String getRegionName(int imageIndex, int regionIndex);

  /**
   * For a particular Region, gets TODO.
   * @param imageIndex index of the Image
   * @param regionIndex index of the Region
   */
  String getRegionTag(int imageIndex, int regionIndex);

  // - Screen property retrieval -

  /**
   * For a particular Screen, gets description of the screen.
   * @param screenIndex index of the Screen
   */
  String getScreenDescription(int screenIndex);

  /**
   * For a particular Screen, gets TODO.
   * @param screenIndex index of the Screen
   */
  String getScreenExtern(int screenIndex);

  /**
   * For a particular Screen, gets unique label identifying the screen.
   * @param screenIndex index of the Screen
   */
  String getScreenID(int screenIndex);

  /**
   * For a particular Screen, gets the screen's name.
   * @param screenIndex index of the Screen
   */
  String getScreenName(int screenIndex);

  /**
   * For a particular Screen, gets description of the screen's protocol.
   * @param screenIndex index of the Screen
   */
  String getScreenProtocolDescription(int screenIndex);

  /**
   * For a particular Screen, gets reference to an externally defined protocol.
   * @param screenIndex index of the Screen
   */
  String getScreenProtocolIdentifier(int screenIndex);

  /**
   * For a particular Screen, gets description of a set of reagents.
   * @param screenIndex index of the Screen
   */
  String getScreenReagentSetDescription(int screenIndex);

  /**
   * For a particular Screen, gets reference to an externally defined set of reagents.
   * @param screenIndex index of the Screen
   */
  String getScreenReagentSetIdentifier(int screenIndex);

  /**
   * For a particular Screen, gets human-readable screen type, e.g. RNAi, cDNA.
   * @param screenIndex index of the Screen
   */
  String getScreenType(int screenIndex);

  // - ScreenAcquisition property retrieval -

  /**
   * For a particular ScreenAcquisition, gets time when the last image was acquired.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, gets unique label identifying the screen's acquisition run.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex);

  /**
   * For a particular ScreenAcquisition, gets time when the first image was acquired.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   */
  String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex);

  // - ScreenRef property retrieval -

  /**
   * For a particular ScreenRef, gets label reference to a screen.
   * @param plateIndex index of the Plate
   * @param screenRefIndex index of the ScreenRef
   */
  String getScreenRefID(int plateIndex, int screenRefIndex);

  // - Shape property retrieval -

  /**
   * For a particular Shape, gets vertical text shift, e.g. sup, sub, normal, -70%. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets text direction, e.g. ltr. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets name of the color with which to fill this ROI. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets opacity (0-100) of the fill color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets rule for filling the ROI, e.g. even-odd, non-zero. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets the name of the font (e.g. "Arial"). This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets size of the font. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets how the font should be stretched, e.g. normal, wider -  EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets font style, e.g. normal, italic. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets font variant, e.g. normal, small-caps. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets font weight, e.g. normal, bold. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets group identifier. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeG(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets unique label identifying the shape.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeID(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets whether or not the ROI can be modified. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets additional stroke information. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets name of the stroke color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets opacity (0-100) of the stroke color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets width (in pixels) of the stroke. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets text label associated with this shape. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeText(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets relative position of the text, e.g. start, middle. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets text decoration, e.g. underline, line-through. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets text color. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets T position of the shape.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getShapeTheT(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets Z position of the shape.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Integer getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets whether or not the ROI should be displayed. This attribute is experimental, and not present in the existing OME-XML schemas..
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  Boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex);

  /**
   * For a particular Shape, gets EXPERIMENTAL.
   * @param imageIndex index of the Image
   * @param roiIndex index of the ROI
   * @param shapeIndex index of the Shape
   */
  String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex);

  // - StageLabel property retrieval -

  /**
   * For a particular StageLabel, gets a name for the stage label.
   * @param imageIndex index of the Image
   */
  String getStageLabelName(int imageIndex);

  /**
   * For a particular StageLabel, gets the x coordinate of the stage.
   * @param imageIndex index of the Image
   */
  Float getStageLabelX(int imageIndex);

  /**
   * For a particular StageLabel, gets the y coordinate of the stage.
   * @param imageIndex index of the Image
   */
  Float getStageLabelY(int imageIndex);

  /**
   * For a particular StageLabel, gets the z coordinate of the stage.
   * @param imageIndex index of the Image
   */
  Float getStageLabelZ(int imageIndex);

  // - StagePosition property retrieval -

  /**
   * For a particular StagePosition, gets the X coordinate of the stage position.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, gets the Y coordinate of the stage position.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex);

  /**
   * For a particular StagePosition, gets the Z coordinate of the stage position.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param planeIndex index of the Plane
   */
  Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex);

  // - Thumbnail property retrieval -

  /**
   * For a particular Thumbnail, gets external URI referring to the thumbnail.
   * @param imageIndex index of the Image
   */
  String getThumbnailHref(int imageIndex);

  /**
   * For a particular Thumbnail, gets unique label identifying the thumbnail.
   * @param imageIndex index of the Image
   */
  String getThumbnailID(int imageIndex);

  /**
   * For a particular Thumbnail, gets MIME-type of the thumbnail; must be set to 'SVG' if thumbnail data is embedded.
   * @param imageIndex index of the Image
   */
  String getThumbnailMIMEtype(int imageIndex);

  // - TiffData property retrieval -

  /**
   * For a particular TiffData, gets the name of the file containing these IFDs.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets C position of the image plane at the specified IFD, indexed from 0.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets T position of the image plane at the specified IFD, indexed from 0.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets Z position of the image plane at the specified IFD, indexed from 0.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets first IFD for which this element is applicable, indexed from 0.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets Number of IFDs for which this element is applicable; default is the number of planes (if no IFD is specified), or 1 (if an IFD is specified)..
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  Integer getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex);

  /**
   * For a particular TiffData, gets unique identifier indicating which file contains these IFDs.
   * @param imageIndex index of the Image
   * @param pixelsIndex index of the Pixels
   * @param tiffDataIndex index of the TiffData
   */
  String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex);

  // - TransmittanceRange property retrieval -

  /**
   * For a particular TransmittanceRange, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  Integer getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  Integer getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  Integer getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  Integer getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);

  /**
   * For a particular TransmittanceRange, gets TODO.
   * @param instrumentIndex index of the Instrument
   * @param filterIndex index of the Filter
   */
  Integer getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);

  // - Well property retrieval -

  /**
   * For a particular Well, gets column index of the well, where top-left is 0.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  Integer getWellColumn(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets description of the externally defined ID for the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellExternalDescription(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets reference to the well in an external database.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellExternalIdentifier(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets unique label identifying the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellID(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets label reference for the associated reagent.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellReagent(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets row index of the well, where top-left is 0.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  Integer getWellRow(int plateIndex, int wellIndex);

  /**
   * For a particular Well, gets human-readable identifier of the screen status, e.g. empty, control.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   */
  String getWellType(int plateIndex, int wellIndex);

  // - WellSample property retrieval -

  /**
   * For a particular WellSample, gets unique label identifying the individual well image.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets label reference for the associated image.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets link to the Image element.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Integer getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets X position of the image within the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets Y position of the image within the well.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex);

  /**
   * For a particular WellSample, gets time-point at which the image started to be collected.
   * @param plateIndex index of the Plate
   * @param wellIndex index of the Well
   * @param wellSampleIndex index of the WellSample
   */
  Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

  // - WellSampleRef property retrieval -

  /**
   * For a particular WellSampleRef, gets TODO.
   * @param screenIndex index of the Screen
   * @param screenAcquisitionIndex index of the ScreenAcquisition
   * @param wellSampleRefIndex index of the WellSampleRef
   */
  String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex);

}
