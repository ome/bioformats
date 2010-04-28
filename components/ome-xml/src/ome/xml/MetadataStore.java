
/*
 * loci.formats.meta.MetadataStore
 *
 *-----------------------------------------------------------------------------
 *
 *  Copyright (C) 2005-@year@ Open Microscopy Environment
 *      Massachusetts Institute of Technology,
 *      National Institutes of Health,
 *      University of Dundee,
 *      University of Wisconsin-Madison
 *
 *
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation; either
 *    version 2.1 of the License, or (at your option) any later version.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with this library; if not, write to the Free Software
 *    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *-----------------------------------------------------------------------------
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by callan via xsd-fu on 2010-04-28 15:55:10+0100
 *
 *-----------------------------------------------------------------------------
 */

// TODO: TEMPORARY, WILL NOT BE USED AFTER TESTING IS COMPLETE
package ome.xml;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

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
public interface MetadataStore
{
	void createRoot();

   	Object getRoot();

	void setRoot(Object root);

	// - Entity storage -

	/** Sets the UUID associated with this collection of metadata. */
	void setUUID(String uuid);

	// AnnotationRef property storage
	// {u'ROI': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'ListAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'Shape': {u'Union': {u'ROI': {u'OME': None}}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference AnnotationRef
	// Arc property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	void setArcID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	void setArcModel(String model, int instrumentIndex, int lightSourceIndex);

	void setArcPower(Double power, int instrumentIndex, int lightSourceIndex);

	void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex);

	// BinData property storage
	// {u'BinaryFile': {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}, u'Mask': {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}, u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? True

	void setBinaryFileBinDataBigEndian(Boolean bigEndian, int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	void setMaskBinDataBigEndian(Boolean bigEndian, int ROIIndex, int shapeIndex, int binDataIndex);

	void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex);

	void setBinaryFileBinDataCompression(Compression compression, int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	void setMaskBinDataCompression(Compression compression, int ROIIndex, int shapeIndex, int binDataIndex);

	void setPixelsBinDataCompression(Compression compression, int imageIndex, int binDataIndex);

	void setBinaryFileBinDataLength(NonNegativeInteger length, int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	void setMaskBinDataLength(NonNegativeInteger length, int ROIIndex, int shapeIndex, int binDataIndex);

	void setPixelsBinDataLength(NonNegativeInteger length, int imageIndex, int binDataIndex);

	// BinaryFile property storage
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	void setFileAnnotationBinaryFileBinData(String binData, int fileAnnotationIndex);

	void setOTFBinaryFileBinData(String binData, int instrumentIndex, int OTFIndex);

	void setFileAnnotationBinaryFileExternal(String external, int fileAnnotationIndex);

	void setOTFBinaryFileExternal(String external, int instrumentIndex, int OTFIndex);

	void setFileAnnotationBinaryFileFileName(String fileName, int fileAnnotationIndex);

	void setOTFBinaryFileFileName(String fileName, int instrumentIndex, int OTFIndex);

	void setFileAnnotationBinaryFileMIMEType(String mimetype, int fileAnnotationIndex);

	void setOTFBinaryFileMIMEType(String mimetype, int instrumentIndex, int OTFIndex);

	void setFileAnnotationBinaryFileSize(Integer size, int fileAnnotationIndex);

	void setOTFBinaryFileSize(Integer size, int instrumentIndex, int OTFIndex);

	// BooleanAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setBooleanAnnotationID(String id, int booleanAnnotationIndex);

	void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex);

	void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex);

	// Channel property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex);

	void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex);

	void setChannelColor(Integer color, int imageIndex, int channelIndex);

	void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex);

	void setChannelDetectorSettings(String detectorSettings, int imageIndex, int channelIndex);

	void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex);

	void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex);

	void setChannelFilterSetRef(String filterSet, int imageIndex, int channelIndex);

	void setChannelFluor(String fluor, int imageIndex, int channelIndex);

	void setChannelID(String id, int imageIndex, int channelIndex);

	void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex);

	void setChannelLightPath(String lightPath, int imageIndex, int channelIndex);

	void setChannelLightSourceSettings(String lightSourceSettings, int imageIndex, int channelIndex);

	void setChannelNDFilter(Double ndfilter, int imageIndex, int channelIndex);

	void setChannelName(String name, int imageIndex, int channelIndex);

	void setChannelOTFRef(String otf, int imageIndex, int channelIndex);

	void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex);

	void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex);

	void setChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int channelIndex);

	// ChannelProfile property storage
	// {u'ProfileSet': None}
	// Is multi path? False

	void setChannelProfileDescription(String description, int channelProfileIndex);

	void setChannelProfileDetectorSettings(String detectorSettings, int channelProfileIndex);

	void setChannelProfileFilterSetRef(String filterSet, int channelProfileIndex);

	void setChannelProfileLightSourceSettings(String lightSourceSettings, int channelProfileIndex);

	void setChannelProfileName(String name, int channelProfileIndex);

	void setChannelProfileOTFRef(String otf, int channelProfileIndex);

	void setChannelProfileOrigin(ProfileSource origin, int channelProfileIndex);

	// Contact property storage
	// {u'Group': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Contact
	// Dataset property storage
	// {u'OME': None}
	// Is multi path? False

	void setDatasetAnnotationRef(String annotation, int datasetIndex, int annotationRefIndex);

	void setDatasetDescription(String description, int datasetIndex);

	void setDatasetExperimenterRef(String experimenter, int datasetIndex);

	void setDatasetGroupRef(String group, int datasetIndex);

	void setDatasetID(String id, int datasetIndex);

	void setDatasetName(String name, int datasetIndex);

	void setDatasetProjectRef(String project, int datasetIndex, int projectRefIndex);

	// DatasetRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference DatasetRef
	// Detector property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex);

	void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex);

	void setDetectorID(String id, int instrumentIndex, int detectorIndex);

	void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex);

	void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

	void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

	void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex);

	void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

	void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex);

	void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex);

	void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex);

	// DetectorSettings property storage
	// {u'ChannelProfile': {u'ProfileSet': None}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	void setChannelProfileDetectorSettingsBinning(Binning binning, int channelProfileIndex);

	void setChannelDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex);

	void setChannelProfileDetectorSettingsGain(Double gain, int channelProfileIndex);

	void setChannelDetectorSettingsGain(Double gain, int imageIndex, int channelIndex);

	void setChannelProfileDetectorSettingsID(String id, int channelProfileIndex);

	void setChannelDetectorSettingsID(String id, int imageIndex, int channelIndex);

	void setChannelProfileDetectorSettingsOffset(Double offset, int channelProfileIndex);

	void setChannelDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex);

	void setChannelProfileDetectorSettingsReadOutRate(Double readOutRate, int channelProfileIndex);

	void setChannelDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex);

	void setChannelProfileDetectorSettingsVoltage(Double voltage, int channelProfileIndex);

	void setChannelDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex);

	// Dichroic property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setDichroicID(String id, int instrumentIndex, int dichroicIndex);

	void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex);

	void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex);

	void setDichroicModel(String model, int instrumentIndex, int dichroicIndex);

	void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex);

	// DichroicRef property storage
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference DichroicRef
	// DoubleAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setDoubleAnnotationID(String id, int doubleAnnotationIndex);

	void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex);

	void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex);

	// Ellipse property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setEllipseDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setEllipseFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setEllipseFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setEllipseID(String id, int ROIIndex, int shapeIndex);

	void setEllipseLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setEllipseName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setEllipseStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setEllipseStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setEllipseTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setEllipseTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setEllipseTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setEllipseTransform(String transform, int ROIIndex, int shapeIndex);

	void setEllipseRadiusX(Double radiusX, int ROIIndex, int shapeIndex);

	void setEllipseRadiusY(Double radiusY, int ROIIndex, int shapeIndex);

	void setEllipseX(Double x, int ROIIndex, int shapeIndex);

	void setEllipseY(Double y, int ROIIndex, int shapeIndex);

	// EmissionFilterRef property storage
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// EmissionFilterRef property storage
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// ExcitationFilterRef property storage
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// ExcitationFilterRef property storage
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Experiment property storage
	// {u'OME': None}
	// Is multi path? False

	void setExperimentDescription(String description, int experimentIndex);

	void setExperimentExperimenterRef(String experimenter, int experimentIndex);

	void setExperimentID(String id, int experimentIndex);

	void setExperimentMicrobeamManipulation(String microbeamManipulation, int experimentIndex, int microbeamManipulationIndex);

	void setExperimentType(ExperimentType type, int experimentIndex);

	// ExperimentRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ExperimentRef
	// Experimenter property storage
	// {u'OME': None}
	// Is multi path? False

	void setExperimenterAnnotationRef(String annotation, int experimenterIndex, int annotationRefIndex);

	void setExperimenterDisplayName(String displayName, int experimenterIndex);

	void setExperimenterEmail(String email, int experimenterIndex);

	void setExperimenterFirstName(String firstName, int experimenterIndex);

	void setExperimenterGroupRef(String group, int experimenterIndex, int groupRefIndex);

	void setExperimenterID(String id, int experimenterIndex);

	void setExperimenterInstitution(String institution, int experimenterIndex);

	void setExperimenterLastName(String lastName, int experimenterIndex);

	void setExperimenterMiddleName(String middleName, int experimenterIndex);

	void setExperimenterUserName(String userName, int experimenterIndex);

	// ExperimenterRef property storage
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ExperimenterRef
	// External property storage
	// {u'BinaryFile': {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}}
	// Is multi path? True

	void setBinaryFileExternalCompression(Compression compression, int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	void setBinaryFileExternalSHA1(String sha1, int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	void setBinaryFileExternalhref(String href, int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	// Filament property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	void setFilamentID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex);

	void setFilamentPower(Double power, int instrumentIndex, int lightSourceIndex);

	void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex);

	// FileAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setFileAnnotationBinaryFile(String binaryFile, int fileAnnotationIndex);

	void setFileAnnotationID(String id, int fileAnnotationIndex);

	void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex);

	// Filter property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex);

	void setFilterID(String id, int instrumentIndex, int filterIndex);

	void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

	void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

	void setFilterModel(String model, int instrumentIndex, int filterIndex);

	void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex);

	void setFilterTransmittanceRange(String transmittanceRange, int instrumentIndex, int filterIndex);

	void setFilterType(FilterType type, int instrumentIndex, int filterIndex);

	// FilterSet property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setFilterSetDichroicRef(String dichroic, int instrumentIndex, int filterSetIndex);

	void setFilterSetEmissionFilterRef(String emissionFilter, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex);

	void setFilterSetExcitationFilterRef(String excitationFilter, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex);

	void setFilterSetID(String id, int instrumentIndex, int filterSetIndex);

	void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex);

	void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex);

	void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex);

	void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex);

	// FilterSetRef property storage
	// {u'ChannelProfile': {u'ProfileSet': None}, u'OTF': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference FilterSetRef
	// Group property storage
	// {u'OME': None}
	// Is multi path? False

	void setGroupContact(String contact, int groupIndex);

	void setGroupDescription(String description, int groupIndex);

	void setGroupID(String id, int groupIndex);

	void setGroupLeader(String leader, int groupIndex);

	void setGroupName(String name, int groupIndex);

	// GroupRef property storage
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Experimenter': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference GroupRef
	// Image property storage
	// {u'OME': None}
	// Is multi path? False

	void setImageAcquiredDate(String acquiredDate, int imageIndex);

	void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex);

	void setImageDatasetRef(String dataset, int imageIndex, int datasetRefIndex);

	void setImageDescription(String description, int imageIndex);

	void setImageExperimentRef(String experiment, int imageIndex);

	void setImageExperimenterRef(String experimenter, int imageIndex);

	void setImageGroupRef(String group, int imageIndex);

	void setImageID(String id, int imageIndex);

	void setImageImagingEnvironment(String imagingEnvironment, int imageIndex);

	void setImageInstrumentRef(String instrument, int imageIndex);

	void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex);

	void setImageName(String name, int imageIndex);

	void setImageObjectiveSettings(String objectiveSettings, int imageIndex);

	void setImagePixels(String pixels, int imageIndex);

	void setImageROIRef(String roi, int imageIndex, int ROIRefIndex);

	void setImageStageLabel(String stageLabel, int imageIndex);

	// ImageProfile property storage
	// {u'ProfileSet': None}
	// Is multi path? False

	void setImageProfileDescription(String description, int imageProfileIndex);

	void setImageProfileInstrumentRef(String instrument, int imageProfileIndex);

	void setImageProfileName(String name, int imageProfileIndex);

	void setImageProfileObjectiveSettings(String objectiveSettings, int imageProfileIndex);

	void setImageProfileorigin(ProfileSource origin, int imageProfileIndex);

	// ImageRef property storage
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ImageRef
	// ImagingEnvironment property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex);

	void setImagingEnvironmentCO2Percent(String co2percent, int imageIndex);

	void setImagingEnvironmentHumidity(String humidity, int imageIndex);

	void setImagingEnvironmentTemperature(Double temperature, int imageIndex);

	// Instrument property storage
	// {u'OME': None}
	// Is multi path? False

	void setInstrumentDetector(String detector, int instrumentIndex, int detectorIndex);

	void setInstrumentDichroic(String dichroic, int instrumentIndex, int dichroicIndex);

	void setInstrumentFilter(String filter, int instrumentIndex, int filterIndex);

	void setInstrumentFilterSet(String filterSet, int instrumentIndex, int filterSetIndex);

	void setInstrumentID(String id, int instrumentIndex);

	void setInstrumentLightSource(String lightSource, int instrumentIndex, int lightSourceIndex);

	void setInstrumentMicroscope(String microscope, int instrumentIndex);

	void setInstrumentOTF(String otf, int instrumentIndex, int OTFIndex);

	void setInstrumentObjective(String objective, int instrumentIndex, int objectiveIndex);

	// InstrumentRef property storage
	// {u'Image': {u'OME': None}, u'ImageProfile': {u'ProfileSet': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference InstrumentRef
	// Laser property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	void setLaserID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	void setLaserModel(String model, int instrumentIndex, int lightSourceIndex);

	void setLaserPower(Double power, int instrumentIndex, int lightSourceIndex);

	void setLaserSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

	void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int lightSourceIndex);

	void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex);

	void setLaserPulse(Pulse pulse, int instrumentIndex, int lightSourceIndex);

	void setLaserPump(String pump, int instrumentIndex, int lightSourceIndex);

	void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int lightSourceIndex);

	void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex);

	void setLaserType(LaserType type, int instrumentIndex, int lightSourceIndex);

	void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int lightSourceIndex);

	// Leader property storage
	// {u'Group': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Leader
	// LightEmittingDiode property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex);

	void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightSourceIndex);

	void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	// LightPath property storage
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	void setLightPathDichroicRef(String dichroic, int imageIndex, int channelIndex);

	void setLightPathEmissionFilterRef(String emissionFilter, int imageIndex, int channelIndex, int emissionFilterRefIndex);

	void setLightPathExcitationFilterRef(String excitationFilter, int imageIndex, int channelIndex, int excitationFilterRefIndex);

	// LightSourceSettings property storage
	// {u'ChannelProfile': {u'ProfileSet': None}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	void setChannelProfileLightSourceSettingsAttenuation(String attenuation, int channelProfileIndex);

	void setChannelLightSourceSettingsAttenuation(String attenuation, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsAttenuation(String attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	void setChannelProfileLightSourceSettingsID(String id, int channelProfileIndex);

	void setChannelLightSourceSettingsID(String id, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	void setChannelProfileLightSourceSettingsWavelength(PositiveInteger wavelength, int channelProfileIndex);

	void setChannelLightSourceSettingsWavelength(PositiveInteger wavelength, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	// Line property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setLineDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setLineFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setLineFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setLineID(String id, int ROIIndex, int shapeIndex);

	void setLineLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setLineName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setLineStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setLineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setLineTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setLineTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setLineTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setLineTransform(String transform, int ROIIndex, int shapeIndex);

	void setLineX1(Double x1, int ROIIndex, int shapeIndex);

	void setLineX2(Double x2, int ROIIndex, int shapeIndex);

	void setLineY1(Double y1, int ROIIndex, int shapeIndex);

	void setLineY2(Double y2, int ROIIndex, int shapeIndex);

	// ListAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setListAnnotationAnnotationRef(String annotation, int listAnnotationIndex, int annotationRefIndex);

	void setListAnnotationID(String id, int listAnnotationIndex);

	void setListAnnotationNamespace(String namespace, int listAnnotationIndex);

	// LongAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setLongAnnotationID(String id, int longAnnotationIndex);

	void setLongAnnotationNamespace(String namespace, int longAnnotationIndex);

	void setLongAnnotationValue(Long value, int longAnnotationIndex);

	// Mask property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setMaskDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setMaskFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setMaskFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setMaskID(String id, int ROIIndex, int shapeIndex);

	void setMaskLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setMaskName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setMaskStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setMaskStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setMaskTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setMaskTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setMaskTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setMaskTransform(String transform, int ROIIndex, int shapeIndex);

	void setMaskBinData(String binData, int ROIIndex, int shapeIndex, int binDataIndex);

	void setMaskX(Double x, int ROIIndex, int shapeIndex);

	void setMaskY(Double y, int ROIIndex, int shapeIndex);

	// MetadataOnly property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	// MicrobeamManipulation property storage
	// {u'Experiment': {u'OME': None}}
	// Is multi path? False

	void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex);

	void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex);

	void setMicrobeamManipulationLightSourceSettings(String lightSourceSettings, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	void setMicrobeamManipulationROIRef(String roi, int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex);

	void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex);

	// MicrobeamManipulationRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference MicrobeamManipulationRef
	// Microscope property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setMicroscopeLotNumber(String lotNumber, int instrumentIndex);

	void setMicroscopeManufacturer(String manufacturer, int instrumentIndex);

	void setMicroscopeModel(String model, int instrumentIndex);

	void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex);

	void setMicroscopeType(MicroscopeType type, int instrumentIndex);

	// OTF property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setOTFBinaryFile(String binaryFile, int instrumentIndex, int OTFIndex);

	void setOTFFilterSetRef(String filterSet, int instrumentIndex, int OTFIndex);

	void setOTFID(String id, int instrumentIndex, int OTFIndex);

	void setOTFObjectiveSettings(String objectiveSettings, int instrumentIndex, int OTFIndex);

	void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int OTFIndex);

	void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int OTFIndex);

	void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int OTFIndex);

	void setOTFType(PixelType type, int instrumentIndex, int OTFIndex);

	// OTFRef property storage
	// {u'ChannelProfile': {u'ProfileSet': None}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference OTFRef
	// Objective property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex);

	void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex);

	void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

	void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex);

	void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex);

	void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex);

	void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex);

	void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

	void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

	void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex);

	void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

	void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex);

	// ObjectiveSettings property storage
	// {u'Image': {u'OME': None}, u'ImageProfile': {u'ProfileSet': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	void setImageObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex);

	void setImageProfileObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageProfileIndex);

	void setOTFObjectiveSettingsCorrectionCollar(Double correctionCollar, int instrumentIndex, int OTFIndex);

	void setImageObjectiveSettingsID(String id, int imageIndex);

	void setImageProfileObjectiveSettingsID(String id, int imageProfileIndex);

	void setOTFObjectiveSettingsID(String id, int instrumentIndex, int OTFIndex);

	void setImageObjectiveSettingsMedium(Medium medium, int imageIndex);

	void setImageProfileObjectiveSettingsMedium(Medium medium, int imageProfileIndex);

	void setOTFObjectiveSettingsMedium(Medium medium, int instrumentIndex, int OTFIndex);

	void setImageObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex);

	void setImageProfileObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageProfileIndex);

	void setOTFObjectiveSettingsRefractiveIndex(Double refractiveIndex, int instrumentIndex, int OTFIndex);

	// Path property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setPathDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setPathFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setPathFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setPathID(String id, int ROIIndex, int shapeIndex);

	void setPathLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setPathName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setPathStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setPathStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setPathStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setPathTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setPathTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setPathTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setPathTransform(String transform, int ROIIndex, int shapeIndex);

	void setPathDefinition(String definition, int ROIIndex, int shapeIndex);

	// Pixels property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setPixelsAnnotationRef(String annotation, int imageIndex, int annotationRefIndex);

	void setPixelsBinData(String binData, int imageIndex, int binDataIndex);

	void setPixelsChannel(String channel, int imageIndex, int channelIndex);

	void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex);

	void setPixelsID(String id, int imageIndex);

	void setPixelsMetadataOnly(String metadataOnly, int imageIndex);

	void setPixelsPhysicalSizeX(Double physicalSizeX, int imageIndex);

	void setPixelsPhysicalSizeY(Double physicalSizeY, int imageIndex);

	void setPixelsPhysicalSizeZ(Double physicalSizeZ, int imageIndex);

	void setPixelsPlane(String plane, int imageIndex, int planeIndex);

	void setPixelsSizeC(PositiveInteger sizeC, int imageIndex);

	void setPixelsSizeT(PositiveInteger sizeT, int imageIndex);

	void setPixelsSizeX(PositiveInteger sizeX, int imageIndex);

	void setPixelsSizeY(PositiveInteger sizeY, int imageIndex);

	void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex);

	void setPixelsTiffData(String tiffData, int imageIndex, int tiffDataIndex);

	void setPixelsTimeIncrement(Double timeIncrement, int imageIndex);

	void setPixelsType(PixelType type, int imageIndex);

	// Plane property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setPlaneAnnotationRef(String annotation, int imageIndex, int planeIndex, int annotationRefIndex);

	void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex);

	void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex);

	void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex);

	void setPlanePositionX(Double positionX, int imageIndex, int planeIndex);

	void setPlanePositionY(Double positionY, int imageIndex, int planeIndex);

	void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex);

	void setPlaneTheC(Integer theC, int imageIndex, int planeIndex);

	void setPlaneTheT(Integer theT, int imageIndex, int planeIndex);

	void setPlaneTheZ(Integer theZ, int imageIndex, int planeIndex);

	// Plate property storage
	// {u'OME': None}
	// Is multi path? False

	void setPlateAnnotationRef(String annotation, int plateIndex, int annotationRefIndex);

	void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex);

	void setPlateColumns(Integer columns, int plateIndex);

	void setPlateDescription(String description, int plateIndex);

	void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);

	void setPlateID(String id, int plateIndex);

	void setPlateName(String name, int plateIndex);

	void setPlatePlateAcquisition(String plateAcquisition, int plateIndex, int plateAcquisitionIndex);

	void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex);

	void setPlateRows(Integer rows, int plateIndex);

	void setPlateScreenRef(String screen, int plateIndex, int screenRefIndex);

	void setPlateStatus(String status, int plateIndex);

	void setPlateWell(String well, int plateIndex, int wellIndex);

	void setPlateWellOriginX(Double wellOriginX, int plateIndex);

	void setPlateWellOriginY(Double wellOriginY, int plateIndex);

	// PlateAcquisition property storage
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	void setPlateAcquisitionAnnotationRef(String annotation, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex);

	void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionMaximumFieldCount(Integer maximumFieldCount, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

	// PlateRef property storage
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference PlateRef
	// Point property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setPointDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setPointFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setPointFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setPointID(String id, int ROIIndex, int shapeIndex);

	void setPointLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setPointName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setPointStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setPointStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setPointTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setPointTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setPointTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setPointTransform(String transform, int ROIIndex, int shapeIndex);

	void setPointX(Double x, int ROIIndex, int shapeIndex);

	void setPointY(Double y, int ROIIndex, int shapeIndex);

	// Polyline property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setPolylineDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setPolylineFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setPolylineFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setPolylineID(String id, int ROIIndex, int shapeIndex);

	void setPolylineLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setPolylineName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setPolylineStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setPolylineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setPolylineTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setPolylineTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setPolylineTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setPolylineTransform(String transform, int ROIIndex, int shapeIndex);

	void setPolylineClosed(Boolean closed, int ROIIndex, int shapeIndex);

	void setPolylinePoints(String points, int ROIIndex, int shapeIndex);

	// Project property storage
	// {u'OME': None}
	// Is multi path? False

	void setProjectAnnotationRef(String annotation, int projectIndex, int annotationRefIndex);

	void setProjectDescription(String description, int projectIndex);

	void setProjectExperimenterRef(String experimenter, int projectIndex);

	void setProjectGroupRef(String group, int projectIndex);

	void setProjectID(String id, int projectIndex);

	void setProjectName(String name, int projectIndex);

	// ProjectRef property storage
	// {u'Dataset': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ProjectRef
	// Pump property storage
	// {u'Laser': {u'LightSource': {u'Instrument': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Pump
	// ROI property storage
	// {u'OME': None}
	// Is multi path? False

	void setROIAnnotationRef(String annotation, int ROIIndex, int annotationRefIndex);

	void setROIDescription(String description, int ROIIndex);

	void setROIID(String id, int ROIIndex);

	void setROIName(String name, int ROIIndex);

	void setROINamespace(String namespace, int ROIIndex);

	void setROIUnion(String union, int ROIIndex);

	// ROIRef property storage
	// {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ROIRef
	// Reagent property storage
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	void setReagentAnnotationRef(String annotation, int screenIndex, int reagentIndex, int annotationRefIndex);

	void setReagentDescription(String description, int screenIndex, int reagentIndex);

	void setReagentID(String id, int screenIndex, int reagentIndex);

	void setReagentName(String name, int screenIndex, int reagentIndex);

	void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

	// ReagentRef property storage
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ReagentRef
	// Rectangle property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setRectangleDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setRectangleFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setRectangleFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setRectangleID(String id, int ROIIndex, int shapeIndex);

	void setRectangleLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setRectangleName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setRectangleStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setRectangleStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setRectangleTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setRectangleTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setRectangleTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setRectangleTransform(String transform, int ROIIndex, int shapeIndex);

	void setRectangleHeight(Double height, int ROIIndex, int shapeIndex);

	void setRectangleWidth(Double width, int ROIIndex, int shapeIndex);

	void setRectangleX(Double x, int ROIIndex, int shapeIndex);

	void setRectangleY(Double y, int ROIIndex, int shapeIndex);

	// Screen property storage
	// {u'OME': None}
	// Is multi path? False

	void setScreenAnnotationRef(String annotation, int screenIndex, int annotationRefIndex);

	void setScreenDescription(String description, int screenIndex);

	void setScreenID(String id, int screenIndex);

	void setScreenName(String name, int screenIndex);

	void setScreenPlateRef(String plate, int screenIndex, int plateRefIndex);

	void setScreenProtocolDescription(String protocolDescription, int screenIndex);

	void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);

	void setScreenReagent(String reagent, int screenIndex, int reagentIndex);

	void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

	void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex);

	void setScreenType(String type, int screenIndex);

	// ScreenRef property storage
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ScreenRef
	// StageLabel property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setStageLabelName(String name, int imageIndex);

	void setStageLabelX(Double x, int imageIndex);

	void setStageLabelY(Double y, int imageIndex);

	void setStageLabelZ(Double z, int imageIndex);

	// StringAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setStringAnnotationID(String id, int stringAnnotationIndex);

	void setStringAnnotationNamespace(String namespace, int stringAnnotationIndex);

	void setStringAnnotationValue(String value, int stringAnnotationIndex);

	// StructuredAnnotations property storage
	// {u'OME': None}
	// Is multi path? False

	void setStructuredAnnotationsBooleanAnnotation(String booleanAnnotation);

	void setStructuredAnnotationsDoubleAnnotation(String doubleAnnotation);

	void setStructuredAnnotationsFileAnnotation(String fileAnnotation);

	void setStructuredAnnotationsListAnnotation(String listAnnotation);

	void setStructuredAnnotationsLongAnnotation(String longAnnotation);

	void setStructuredAnnotationsStringAnnotation(String stringAnnotation);

	void setStructuredAnnotationsTimestampAnnotation(String timestampAnnotation);

	void setStructuredAnnotationsXMLAnnotation(String xmlannotation);

	// Text property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	void setTextDescription(String description, int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	void setTextFill(Integer fill, int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	void setTextFontSize(Integer fontSize, int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	void setTextID(String id, int ROIIndex, int shapeIndex);

	void setTextLabel(String label, int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	void setTextName(String name, int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	void setTextStroke(Integer stroke, int ROIIndex, int shapeIndex);

	void setTextStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	void setTextStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	void setTextTheC(Integer theC, int ROIIndex, int shapeIndex);

	void setTextTheT(Integer theT, int ROIIndex, int shapeIndex);

	void setTextTheZ(Integer theZ, int ROIIndex, int shapeIndex);

	void setTextTransform(String transform, int ROIIndex, int shapeIndex);

	void setTextValue(String value, int ROIIndex, int shapeIndex);

	void setTextX(Double x, int ROIIndex, int shapeIndex);

	void setTextY(Double y, int ROIIndex, int shapeIndex);

	// TiffData property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setTiffDataFirstC(Integer firstC, int imageIndex, int tiffDataIndex);

	void setTiffDataFirstT(Integer firstT, int imageIndex, int tiffDataIndex);

	void setTiffDataFirstZ(Integer firstZ, int imageIndex, int tiffDataIndex);

	void setTiffDataIFD(Integer ifd, int imageIndex, int tiffDataIndex);

	void setTiffDataPlaneCount(Integer planeCount, int imageIndex, int tiffDataIndex);

	void setTiffDataUUID(String uuid, int imageIndex, int tiffDataIndex);

	// TimestampAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setTimestampAnnotationID(String id, int timestampAnnotationIndex);

	void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex);

	void setTimestampAnnotationValue(String value, int timestampAnnotationIndex);

	// TransmittanceRange property storage
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	void setTransmittanceRangeCutIn(Integer cutIn, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutInTolerance(Integer cutInTolerance, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutOut(Integer cutOut, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutOutTolerance(Integer cutOutTolerance, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeTransmittance(String transmittance, int instrumentIndex, int filterIndex);

	// UUID property storage
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex);

	// Union property storage
	// {u'ROI': {u'OME': None}}
	// Is multi path? False

	void setUnionShape(String shape, int ROIIndex, int shapeIndex);

	// Well property storage
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	void setWellAnnotationRef(String annotation, int plateIndex, int wellIndex, int annotationRefIndex);

	void setWellColor(Integer color, int plateIndex, int wellIndex);

	void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex);

	void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);

	void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);

	void setWellID(String id, int plateIndex, int wellIndex);

	void setWellReagentRef(String reagent, int plateIndex, int wellIndex);

	void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex);

	void setWellStatus(String status, int plateIndex, int wellIndex);

	void setWellWellSample(String wellSample, int plateIndex, int wellIndex, int wellSampleIndex);

	// WellSample property storage
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	void setWellSampleAnnotationRef(String annotation, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex);

	void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

	// WellSampleRef property storage
	// {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference WellSampleRef
	// XMLAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setXMLAnnotationID(String id, int XMLAnnotationIndex);

	void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex);

	void setXMLAnnotationValue(String value, int XMLAnnotationIndex);

}
