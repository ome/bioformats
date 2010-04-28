
/*
 * loci.formats.meta.MetadataRetrieve
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
 * Created by callan via xsd-fu on 2010-04-28 16:12:52+0100
 *
 *-----------------------------------------------------------------------------
 */

// TODO: TEMPORARY, WILL NOT BE USED AFTER TESTING IS COMPLETE
package ome.xml;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

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

	// -- Entity counting --

	// AnnotationRef entity counting
	int getROIAnnotationRefCount(int ROIIndex);

	int getReagentAnnotationRefCount(int screenIndex, int reagentIndex);

	int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex);

	int getPlateAnnotationRefCount(int plateIndex);

	int getImageAnnotationRefCount(int imageIndex);

	int getScreenAnnotationRefCount(int screenIndex);

	int getWellAnnotationRefCount(int plateIndex, int wellIndex);

	int getDatasetAnnotationRefCount(int datasetIndex);

	int getProjectAnnotationRefCount(int projectIndex);

	int getListAnnotationAnnotationRefCount(int listAnnotationIndex);

	int getShapeAnnotationRefCount(int ROIIndex, int shapeIndex);

	int getPlaneAnnotationRefCount(int imageIndex, int planeIndex);

	int getExperimenterAnnotationRefCount(int experimenterIndex);

	int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex);

	int getPixelsAnnotationRefCount(int imageIndex);

	int getChannelAnnotationRefCount(int imageIndex, int channelIndex);

	// Arc entity counting
	// BinData entity counting
	int getMaskBinDataCount(int ROIIndex, int shapeIndex);

	int getPixelsBinDataCount(int imageIndex);

	// BinaryFile entity counting
	// BooleanAnnotation entity counting
	int getBooleanAnnotationCount();

	// Channel entity counting
	int getChannelCount(int imageIndex);

	// ChannelProfile entity counting
	int getChannelProfileCount();

	// Contact entity counting
	// Dataset entity counting
	int getDatasetCount();

	// DatasetRef entity counting
	int getDatasetRefCount(int imageIndex);

	// Detector entity counting
	int getDetectorCount(int instrumentIndex);

	// DetectorSettings entity counting
	// Dichroic entity counting
	int getDichroicCount(int instrumentIndex);

	// DichroicRef entity counting
	// DoubleAnnotation entity counting
	int getDoubleAnnotationCount();

	// Ellipse entity counting
	// EmissionFilterRef entity counting
	int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex);

	int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex);

	// ExcitationFilterRef entity counting
	int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex);

	int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex);

	// Experiment entity counting
	int getExperimentCount();

	// ExperimentRef entity counting
	// Experimenter entity counting
	int getExperimenterCount();

	// ExperimenterRef entity counting
	// External entity counting
	// Filament entity counting
	// FileAnnotation entity counting
	int getFileAnnotationCount();

	// Filter entity counting
	int getFilterCount(int instrumentIndex);

	// FilterSet entity counting
	int getFilterSetCount(int instrumentIndex);

	// FilterSetRef entity counting
	// Group entity counting
	int getGroupCount();

	// GroupRef entity counting
	int getExperimenterGroupRefCount(int experimenterIndex);

	// Image entity counting
	int getImageCount();

	// ImageProfile entity counting
	int getImageProfileCount();

	// ImageRef entity counting
	// ImagingEnvironment entity counting
	// Instrument entity counting
	int getInstrumentCount();

	// InstrumentRef entity counting
	// Laser entity counting
	// Leader entity counting
	// LightEmittingDiode entity counting
	// LightPath entity counting
	// LightSourceSettings entity counting
	int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex);

	// Line entity counting
	// ListAnnotation entity counting
	int getListAnnotationCount();

	// LongAnnotation entity counting
	int getLongAnnotationCount();

	// Mask entity counting
	// MetadataOnly entity counting
	// MicrobeamManipulation entity counting
	int getMicrobeamManipulationCount(int experimentIndex);

	// MicrobeamManipulationRef entity counting
	int getMicrobeamManipulationRefCount(int imageIndex);

	// Microscope entity counting
	// OTF entity counting
	int getOTFCount(int instrumentIndex);

	// OTFRef entity counting
	// Objective entity counting
	int getObjectiveCount(int instrumentIndex);

	// ObjectiveSettings entity counting
	// Path entity counting
	// Pixels entity counting
	// Plane entity counting
	int getPlaneCount(int imageIndex);

	// Plate entity counting
	int getPlateCount();

	// PlateAcquisition entity counting
	int getPlateAcquisitionCount(int plateIndex);

	// PlateRef entity counting
	int getPlateRefCount(int screenIndex);

	// Point entity counting
	// Polyline entity counting
	// Project entity counting
	int getProjectCount();

	// ProjectRef entity counting
	int getProjectRefCount(int datasetIndex);

	// Pump entity counting
	// ROI entity counting
	int getROICount();

	// ROIRef entity counting
	int getImageROIRefCount(int imageIndex);

	int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex);

	// Reagent entity counting
	int getReagentCount(int screenIndex);

	// ReagentRef entity counting
	// Rectangle entity counting
	// Screen entity counting
	int getScreenCount();

	// ScreenRef entity counting
	int getScreenRefCount(int plateIndex);

	// StageLabel entity counting
	// StringAnnotation entity counting
	int getStringAnnotationCount();

	// StructuredAnnotations entity counting
	// Text entity counting
	// TiffData entity counting
	int getTiffDataCount(int imageIndex);

	// TimestampAnnotation entity counting
	int getTimestampAnnotationCount();

	// TransmittanceRange entity counting
	// UUID entity counting
	// Union entity counting
	// Well entity counting
	int getWellCount(int plateIndex);

	// WellSample entity counting
	int getWellSampleCount(int plateIndex, int wellIndex);

	// WellSampleRef entity counting
	int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex);

	// XMLAnnotation entity counting
	int getXMLAnnotationCount();


	// -- Entity retrieval --

	/** Gets the UUID associated with this collection of metadata. */
	String getUUID();

	//
	// AnnotationRef property storage
	//
	// {u'ROI': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'ListAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'Shape': {u'Union': {u'ROI': {u'OME': None}}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference AnnotationRef

	//
	// Arc property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	String getArcID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	String getArcLotNumber(int instrumentIndex, int lightSourceIndex);

	String getArcManufacturer(int instrumentIndex, int lightSourceIndex);

	String getArcModel(int instrumentIndex, int lightSourceIndex);

	Double getArcPower(int instrumentIndex, int lightSourceIndex);

	String getArcSerialNumber(int instrumentIndex, int lightSourceIndex);

	ArcType getArcType(int instrumentIndex, int lightSourceIndex);

	//
	// BinData property storage
	//
	// {u'BinaryFile': {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}, u'Mask': {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}, u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? True

	Boolean getBinaryFileBinDataBigEndian(int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	Boolean getMaskBinDataBigEndian(int ROIIndex, int shapeIndex, int binDataIndex);

	Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex);

	Compression getBinaryFileBinDataCompression(int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	Compression getMaskBinDataCompression(int ROIIndex, int shapeIndex, int binDataIndex);

	Compression getPixelsBinDataCompression(int imageIndex, int binDataIndex);

	NonNegativeInteger getBinaryFileBinDataLength(int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	NonNegativeInteger getMaskBinDataLength(int ROIIndex, int shapeIndex, int binDataIndex);

	NonNegativeInteger getPixelsBinDataLength(int imageIndex, int binDataIndex);

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	String getFileAnnotationBinaryFileBinData(int fileAnnotationIndex);

	String getOTFBinaryFileBinData(int instrumentIndex, int OTFIndex);

	String getFileAnnotationBinaryFileExternal(int fileAnnotationIndex);

	String getOTFBinaryFileExternal(int instrumentIndex, int OTFIndex);

	String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex);

	String getOTFBinaryFileFileName(int instrumentIndex, int OTFIndex);

	String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex);

	String getOTFBinaryFileMIMEType(int instrumentIndex, int OTFIndex);

	Integer getFileAnnotationBinaryFileSize(int fileAnnotationIndex);

	Integer getOTFBinaryFileSize(int instrumentIndex, int OTFIndex);

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getBooleanAnnotationID(int booleanAnnotationIndex);

	String getBooleanAnnotationNamespace(int booleanAnnotationIndex);

	Boolean getBooleanAnnotationValue(int booleanAnnotationIndex);

	//
	// Channel property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex);

	String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex);

	Integer getChannelColor(int imageIndex, int channelIndex);

	ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex);

	String getChannelDetectorSettings(int imageIndex, int channelIndex);

	PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex);

	PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex);

	String getChannelFilterSetRef(int imageIndex, int channelIndex);

	String getChannelFluor(int imageIndex, int channelIndex);

	String getChannelID(int imageIndex, int channelIndex);

	IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex);

	String getChannelLightPath(int imageIndex, int channelIndex);

	String getChannelLightSourceSettings(int imageIndex, int channelIndex);

	Double getChannelNDFilter(int imageIndex, int channelIndex);

	String getChannelName(int imageIndex, int channelIndex);

	String getChannelOTFRef(int imageIndex, int channelIndex);

	Double getChannelPinholeSize(int imageIndex, int channelIndex);

	Integer getChannelPockelCellSetting(int imageIndex, int channelIndex);

	Integer getChannelSamplesPerPixel(int imageIndex, int channelIndex);

	//
	// ChannelProfile property storage
	//
	// {u'ProfileSet': None}
	// Is multi path? False

	String getChannelProfileDescription(int channelProfileIndex);

	String getChannelProfileDetectorSettings(int channelProfileIndex);

	String getChannelProfileFilterSetRef(int channelProfileIndex);

	String getChannelProfileLightSourceSettings(int channelProfileIndex);

	String getChannelProfileName(int channelProfileIndex);

	String getChannelProfileOTFRef(int channelProfileIndex);

	ProfileSource getChannelProfileOrigin(int channelProfileIndex);

	//
	// Contact property storage
	//
	// {u'Group': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Contact

	//
	// Dataset property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex);

	String getDatasetDescription(int datasetIndex);

	String getDatasetExperimenterRef(int datasetIndex);

	String getDatasetGroupRef(int datasetIndex);

	String getDatasetID(int datasetIndex);

	String getDatasetName(int datasetIndex);

	String getDatasetProjectRef(int datasetIndex, int projectRefIndex);

	//
	// DatasetRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference DatasetRef

	//
	// Detector property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);

	Double getDetectorGain(int instrumentIndex, int detectorIndex);

	String getDetectorID(int instrumentIndex, int detectorIndex);

	String getDetectorLotNumber(int instrumentIndex, int detectorIndex);

	String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

	String getDetectorModel(int instrumentIndex, int detectorIndex);

	Double getDetectorOffset(int instrumentIndex, int detectorIndex);

	String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

	DetectorType getDetectorType(int instrumentIndex, int detectorIndex);

	Double getDetectorVoltage(int instrumentIndex, int detectorIndex);

	Double getDetectorZoom(int instrumentIndex, int detectorIndex);

	//
	// DetectorSettings property storage
	//
	// {u'ChannelProfile': {u'ProfileSet': None}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	Binning getChannelProfileDetectorSettingsBinning(int channelProfileIndex);

	Binning getChannelDetectorSettingsBinning(int imageIndex, int channelIndex);

	Double getChannelProfileDetectorSettingsGain(int channelProfileIndex);

	Double getChannelDetectorSettingsGain(int imageIndex, int channelIndex);

	String getChannelProfileDetectorSettingsID(int channelProfileIndex);

	String getChannelDetectorSettingsID(int imageIndex, int channelIndex);

	Double getChannelProfileDetectorSettingsOffset(int channelProfileIndex);

	Double getChannelDetectorSettingsOffset(int imageIndex, int channelIndex);

	Double getChannelProfileDetectorSettingsReadOutRate(int channelProfileIndex);

	Double getChannelDetectorSettingsReadOutRate(int imageIndex, int channelIndex);

	Double getChannelProfileDetectorSettingsVoltage(int channelProfileIndex);

	Double getChannelDetectorSettingsVoltage(int imageIndex, int channelIndex);

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getDichroicID(int instrumentIndex, int dichroicIndex);

	String getDichroicLotNumber(int instrumentIndex, int dichroicIndex);

	String getDichroicManufacturer(int instrumentIndex, int dichroicIndex);

	String getDichroicModel(int instrumentIndex, int dichroicIndex);

	String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex);

	//
	// DichroicRef property storage
	//
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference DichroicRef

	//
	// DoubleAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getDoubleAnnotationID(int doubleAnnotationIndex);

	String getDoubleAnnotationNamespace(int doubleAnnotationIndex);

	Double getDoubleAnnotationValue(int doubleAnnotationIndex);

	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getEllipseDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getEllipseFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getEllipseFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getEllipseID(int ROIIndex, int shapeIndex);

	String getEllipseLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getEllipseName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getEllipseStroke(int ROIIndex, int shapeIndex);

	String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getEllipseTheC(int ROIIndex, int shapeIndex);

	Integer getEllipseTheT(int ROIIndex, int shapeIndex);

	Integer getEllipseTheZ(int ROIIndex, int shapeIndex);

	String getEllipseTransform(int ROIIndex, int shapeIndex);

	Double getEllipseRadiusX(int ROIIndex, int shapeIndex);

	Double getEllipseRadiusY(int ROIIndex, int shapeIndex);

	Double getEllipseX(int ROIIndex, int shapeIndex);

	Double getEllipseY(int ROIIndex, int shapeIndex);

	//
	// EmissionFilterRef property storage
	//
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// EmissionFilterRef property storage
	//
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// ExcitationFilterRef property storage
	//
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// ExcitationFilterRef property storage
	//
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// Experiment property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getExperimentDescription(int experimentIndex);

	String getExperimentExperimenterRef(int experimentIndex);

	String getExperimentID(int experimentIndex);

	String getExperimentMicrobeamManipulation(int experimentIndex, int microbeamManipulationIndex);

	ExperimentType getExperimentType(int experimentIndex);

	//
	// ExperimentRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ExperimentRef

	//
	// Experimenter property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex);

	String getExperimenterDisplayName(int experimenterIndex);

	String getExperimenterEmail(int experimenterIndex);

	String getExperimenterFirstName(int experimenterIndex);

	String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex);

	String getExperimenterID(int experimenterIndex);

	String getExperimenterInstitution(int experimenterIndex);

	String getExperimenterLastName(int experimenterIndex);

	String getExperimenterMiddleName(int experimenterIndex);

	String getExperimenterUserName(int experimenterIndex);

	//
	// ExperimenterRef property storage
	//
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ExperimenterRef

	//
	// External property storage
	//
	// {u'BinaryFile': {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}}
	// Is multi path? True

	Compression getBinaryFileExternalCompression(int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	String getBinaryFileExternalSHA1(int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	String getBinaryFileExternalhref(int fileAnnotationIndex, int instrumentIndex, int OTFIndex);

	//
	// Filament property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	String getFilamentID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex);

	String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex);

	String getFilamentModel(int instrumentIndex, int lightSourceIndex);

	Double getFilamentPower(int instrumentIndex, int lightSourceIndex);

	String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex);

	FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex);

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getFileAnnotationBinaryFile(int fileAnnotationIndex);

	String getFileAnnotationID(int fileAnnotationIndex);

	String getFileAnnotationNamespace(int fileAnnotationIndex);

	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getFilterFilterWheel(int instrumentIndex, int filterIndex);

	String getFilterID(int instrumentIndex, int filterIndex);

	String getFilterLotNumber(int instrumentIndex, int filterIndex);

	String getFilterManufacturer(int instrumentIndex, int filterIndex);

	String getFilterModel(int instrumentIndex, int filterIndex);

	String getFilterSerialNumber(int instrumentIndex, int filterIndex);

	String getFilterTransmittanceRange(int instrumentIndex, int filterIndex);

	FilterType getFilterType(int instrumentIndex, int filterIndex);

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex);

	String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex);

	String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex);

	String getFilterSetID(int instrumentIndex, int filterSetIndex);

	String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);

	String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);

	String getFilterSetModel(int instrumentIndex, int filterSetIndex);

	String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex);

	//
	// FilterSetRef property storage
	//
	// {u'ChannelProfile': {u'ProfileSet': None}, u'OTF': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference FilterSetRef

	//
	// Group property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getGroupContact(int groupIndex);

	String getGroupDescription(int groupIndex);

	String getGroupID(int groupIndex);

	String getGroupLeader(int groupIndex);

	String getGroupName(int groupIndex);

	//
	// GroupRef property storage
	//
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Experimenter': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference GroupRef

	//
	// Image property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getImageAcquiredDate(int imageIndex);

	String getImageAnnotationRef(int imageIndex, int annotationRefIndex);

	String getImageDatasetRef(int imageIndex, int datasetRefIndex);

	String getImageDescription(int imageIndex);

	String getImageExperimentRef(int imageIndex);

	String getImageExperimenterRef(int imageIndex);

	String getImageGroupRef(int imageIndex);

	String getImageID(int imageIndex);

	String getImageImagingEnvironment(int imageIndex);

	String getImageInstrumentRef(int imageIndex);

	String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex);

	String getImageName(int imageIndex);

	String getImageObjectiveSettings(int imageIndex);

	String getImagePixels(int imageIndex);

	String getImageROIRef(int imageIndex, int ROIRefIndex);

	String getImageStageLabel(int imageIndex);

	//
	// ImageProfile property storage
	//
	// {u'ProfileSet': None}
	// Is multi path? False

	String getImageProfileDescription(int imageProfileIndex);

	String getImageProfileInstrumentRef(int imageProfileIndex);

	String getImageProfileName(int imageProfileIndex);

	String getImageProfileObjectiveSettings(int imageProfileIndex);

	ProfileSource getImageProfileorigin(int imageProfileIndex);

	//
	// ImageRef property storage
	//
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ImageRef

	//
	// ImagingEnvironment property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	Double getImagingEnvironmentAirPressure(int imageIndex);

	String getImagingEnvironmentCO2Percent(int imageIndex);

	String getImagingEnvironmentHumidity(int imageIndex);

	Double getImagingEnvironmentTemperature(int imageIndex);

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getInstrumentDetector(int instrumentIndex, int detectorIndex);

	String getInstrumentDichroic(int instrumentIndex, int dichroicIndex);

	String getInstrumentFilter(int instrumentIndex, int filterIndex);

	String getInstrumentFilterSet(int instrumentIndex, int filterSetIndex);

	String getInstrumentID(int instrumentIndex);

	String getInstrumentLightSource(int instrumentIndex, int lightSourceIndex);

	String getInstrumentMicroscope(int instrumentIndex);

	String getInstrumentOTF(int instrumentIndex, int OTFIndex);

	String getInstrumentObjective(int instrumentIndex, int objectiveIndex);

	//
	// InstrumentRef property storage
	//
	// {u'Image': {u'OME': None}, u'ImageProfile': {u'ProfileSet': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference InstrumentRef

	//
	// Laser property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	String getLaserID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	String getLaserLotNumber(int instrumentIndex, int lightSourceIndex);

	String getLaserManufacturer(int instrumentIndex, int lightSourceIndex);

	String getLaserModel(int instrumentIndex, int lightSourceIndex);

	Double getLaserPower(int instrumentIndex, int lightSourceIndex);

	String getLaserSerialNumber(int instrumentIndex, int lightSourceIndex);

	PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);

	LaserMedium getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);

	Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex);

	Pulse getLaserPulse(int instrumentIndex, int lightSourceIndex);

	String getLaserPump(int instrumentIndex, int lightSourceIndex);

	Double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex);

	Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

	LaserType getLaserType(int instrumentIndex, int lightSourceIndex);

	PositiveInteger getLaserWavelength(int instrumentIndex, int lightSourceIndex);

	//
	// Leader property storage
	//
	// {u'Group': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Leader

	//
	// LightEmittingDiode property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	String getLightEmittingDiodeID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex);

	String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex);

	String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex);

	Double getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex);

	String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightSourceIndex);

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	String getLightPathDichroicRef(int imageIndex, int channelIndex);

	String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex);

	String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex);

	//
	// LightSourceSettings property storage
	//
	// {u'ChannelProfile': {u'ProfileSet': None}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	String getChannelProfileLightSourceSettingsAttenuation(int channelProfileIndex);

	String getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex);

	String getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	String getChannelProfileLightSourceSettingsID(int channelProfileIndex);

	String getChannelLightSourceSettingsID(int imageIndex, int channelIndex);

	String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	PositiveInteger getChannelProfileLightSourceSettingsWavelength(int channelProfileIndex);

	PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex);

	PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getLineDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getLineFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getLineFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getLineID(int ROIIndex, int shapeIndex);

	String getLineLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getLineName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getLineStroke(int ROIIndex, int shapeIndex);

	String getLineStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getLineStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getLineTheC(int ROIIndex, int shapeIndex);

	Integer getLineTheT(int ROIIndex, int shapeIndex);

	Integer getLineTheZ(int ROIIndex, int shapeIndex);

	String getLineTransform(int ROIIndex, int shapeIndex);

	Double getLineX1(int ROIIndex, int shapeIndex);

	Double getLineX2(int ROIIndex, int shapeIndex);

	Double getLineY1(int ROIIndex, int shapeIndex);

	Double getLineY2(int ROIIndex, int shapeIndex);

	//
	// ListAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex);

	String getListAnnotationID(int listAnnotationIndex);

	String getListAnnotationNamespace(int listAnnotationIndex);

	//
	// LongAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getLongAnnotationID(int longAnnotationIndex);

	String getLongAnnotationNamespace(int longAnnotationIndex);

	Long getLongAnnotationValue(int longAnnotationIndex);

	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getMaskDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getMaskFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getMaskFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getMaskID(int ROIIndex, int shapeIndex);

	String getMaskLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getMaskName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getMaskStroke(int ROIIndex, int shapeIndex);

	String getMaskStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getMaskStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getMaskTheC(int ROIIndex, int shapeIndex);

	Integer getMaskTheT(int ROIIndex, int shapeIndex);

	Integer getMaskTheZ(int ROIIndex, int shapeIndex);

	String getMaskTransform(int ROIIndex, int shapeIndex);

	String getMaskBinData(int ROIIndex, int shapeIndex, int binDataIndex);

	Double getMaskX(int ROIIndex, int shapeIndex);

	Double getMaskY(int ROIIndex, int shapeIndex);

	//
	// MetadataOnly property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	//
	// MicrobeamManipulation property storage
	//
	// {u'Experiment': {u'OME': None}}
	// Is multi path? False

	String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex);

	String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex);

	String getMicrobeamManipulationLightSourceSettings(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex);

	MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex);

	//
	// MicrobeamManipulationRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference MicrobeamManipulationRef

	//
	// Microscope property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getMicroscopeLotNumber(int instrumentIndex);

	String getMicroscopeManufacturer(int instrumentIndex);

	String getMicroscopeModel(int instrumentIndex);

	String getMicroscopeSerialNumber(int instrumentIndex);

	MicroscopeType getMicroscopeType(int instrumentIndex);

	//
	// OTF property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getOTFBinaryFile(int instrumentIndex, int OTFIndex);

	String getOTFFilterSetRef(int instrumentIndex, int OTFIndex);

	String getOTFID(int instrumentIndex, int OTFIndex);

	String getOTFObjectiveSettings(int instrumentIndex, int OTFIndex);

	Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int OTFIndex);

	PositiveInteger getOTFSizeX(int instrumentIndex, int OTFIndex);

	PositiveInteger getOTFSizeY(int instrumentIndex, int OTFIndex);

	PixelType getOTFType(int instrumentIndex, int OTFIndex);

	//
	// OTFRef property storage
	//
	// {u'ChannelProfile': {u'ProfileSet': None}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference OTFRef

	//
	// Objective property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

	Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

	String getObjectiveID(int instrumentIndex, int objectiveIndex);

	Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

	Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex);

	Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

	String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex);

	String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

	String getObjectiveModel(int instrumentIndex, int objectiveIndex);

	Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

	String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

	Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}, u'ImageProfile': {u'ProfileSet': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	Double getImageObjectiveSettingsCorrectionCollar(int imageIndex);

	Double getImageProfileObjectiveSettingsCorrectionCollar(int imageProfileIndex);

	Double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int OTFIndex);

	String getImageObjectiveSettingsID(int imageIndex);

	String getImageProfileObjectiveSettingsID(int imageProfileIndex);

	String getOTFObjectiveSettingsID(int instrumentIndex, int OTFIndex);

	Medium getImageObjectiveSettingsMedium(int imageIndex);

	Medium getImageProfileObjectiveSettingsMedium(int imageProfileIndex);

	Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int OTFIndex);

	Double getImageObjectiveSettingsRefractiveIndex(int imageIndex);

	Double getImageProfileObjectiveSettingsRefractiveIndex(int imageProfileIndex);

	Double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int OTFIndex);

	//
	// Path property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getPathDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getPathFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getPathFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getPathID(int ROIIndex, int shapeIndex);

	String getPathLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getPathName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getPathStroke(int ROIIndex, int shapeIndex);

	String getPathStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getPathStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getPathTheC(int ROIIndex, int shapeIndex);

	Integer getPathTheT(int ROIIndex, int shapeIndex);

	Integer getPathTheZ(int ROIIndex, int shapeIndex);

	String getPathTransform(int ROIIndex, int shapeIndex);

	String getPathDefinition(int ROIIndex, int shapeIndex);

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex);

	String getPixelsBinData(int imageIndex, int binDataIndex);

	String getPixelsChannel(int imageIndex, int channelIndex);

	DimensionOrder getPixelsDimensionOrder(int imageIndex);

	String getPixelsID(int imageIndex);

	String getPixelsMetadataOnly(int imageIndex);

	Double getPixelsPhysicalSizeX(int imageIndex);

	Double getPixelsPhysicalSizeY(int imageIndex);

	Double getPixelsPhysicalSizeZ(int imageIndex);

	String getPixelsPlane(int imageIndex, int planeIndex);

	PositiveInteger getPixelsSizeC(int imageIndex);

	PositiveInteger getPixelsSizeT(int imageIndex);

	PositiveInteger getPixelsSizeX(int imageIndex);

	PositiveInteger getPixelsSizeY(int imageIndex);

	PositiveInteger getPixelsSizeZ(int imageIndex);

	String getPixelsTiffData(int imageIndex, int tiffDataIndex);

	Double getPixelsTimeIncrement(int imageIndex);

	PixelType getPixelsType(int imageIndex);

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex);

	Double getPlaneDeltaT(int imageIndex, int planeIndex);

	Double getPlaneExposureTime(int imageIndex, int planeIndex);

	String getPlaneHashSHA1(int imageIndex, int planeIndex);

	Double getPlanePositionX(int imageIndex, int planeIndex);

	Double getPlanePositionY(int imageIndex, int planeIndex);

	Double getPlanePositionZ(int imageIndex, int planeIndex);

	Integer getPlaneTheC(int imageIndex, int planeIndex);

	Integer getPlaneTheT(int imageIndex, int planeIndex);

	Integer getPlaneTheZ(int imageIndex, int planeIndex);

	//
	// Plate property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getPlateAnnotationRef(int plateIndex, int annotationRefIndex);

	NamingConvention getPlateColumnNamingConvention(int plateIndex);

	Integer getPlateColumns(int plateIndex);

	String getPlateDescription(int plateIndex);

	String getPlateExternalIdentifier(int plateIndex);

	String getPlateID(int plateIndex);

	String getPlateName(int plateIndex);

	String getPlatePlateAcquisition(int plateIndex, int plateAcquisitionIndex);

	NamingConvention getPlateRowNamingConvention(int plateIndex);

	Integer getPlateRows(int plateIndex);

	String getPlateScreenRef(int plateIndex, int screenRefIndex);

	String getPlateStatus(int plateIndex);

	String getPlateWell(int plateIndex, int wellIndex);

	Double getPlateWellOriginX(int plateIndex);

	Double getPlateWellOriginY(int plateIndex);

	//
	// PlateAcquisition property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex);

	String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex);

	Integer getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

	//
	// PlateRef property storage
	//
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference PlateRef

	//
	// Point property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getPointDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getPointFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getPointFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getPointID(int ROIIndex, int shapeIndex);

	String getPointLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getPointName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getPointStroke(int ROIIndex, int shapeIndex);

	String getPointStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getPointStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getPointTheC(int ROIIndex, int shapeIndex);

	Integer getPointTheT(int ROIIndex, int shapeIndex);

	Integer getPointTheZ(int ROIIndex, int shapeIndex);

	String getPointTransform(int ROIIndex, int shapeIndex);

	Double getPointX(int ROIIndex, int shapeIndex);

	Double getPointY(int ROIIndex, int shapeIndex);

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getPolylineDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getPolylineFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getPolylineFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getPolylineID(int ROIIndex, int shapeIndex);

	String getPolylineLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getPolylineName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getPolylineStroke(int ROIIndex, int shapeIndex);

	String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getPolylineTheC(int ROIIndex, int shapeIndex);

	Integer getPolylineTheT(int ROIIndex, int shapeIndex);

	Integer getPolylineTheZ(int ROIIndex, int shapeIndex);

	String getPolylineTransform(int ROIIndex, int shapeIndex);

	Boolean getPolylineClosed(int ROIIndex, int shapeIndex);

	String getPolylinePoints(int ROIIndex, int shapeIndex);

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getProjectAnnotationRef(int projectIndex, int annotationRefIndex);

	String getProjectDescription(int projectIndex);

	String getProjectExperimenterRef(int projectIndex);

	String getProjectGroupRef(int projectIndex);

	String getProjectID(int projectIndex);

	String getProjectName(int projectIndex);

	//
	// ProjectRef property storage
	//
	// {u'Dataset': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ProjectRef

	//
	// Pump property storage
	//
	// {u'Laser': {u'LightSource': {u'Instrument': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Pump

	//
	// ROI property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getROIAnnotationRef(int ROIIndex, int annotationRefIndex);

	String getROIDescription(int ROIIndex);

	String getROIID(int ROIIndex);

	String getROIName(int ROIIndex);

	String getROINamespace(int ROIIndex);

	String getROIUnion(int ROIIndex);

	//
	// ROIRef property storage
	//
	// {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ROIRef

	//
	// Reagent property storage
	//
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	String getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex);

	String getReagentDescription(int screenIndex, int reagentIndex);

	String getReagentID(int screenIndex, int reagentIndex);

	String getReagentName(int screenIndex, int reagentIndex);

	String getReagentReagentIdentifier(int screenIndex, int reagentIndex);

	//
	// ReagentRef property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ReagentRef

	//
	// Rectangle property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getRectangleDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getRectangleFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getRectangleFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getRectangleID(int ROIIndex, int shapeIndex);

	String getRectangleLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getRectangleName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getRectangleStroke(int ROIIndex, int shapeIndex);

	String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getRectangleTheC(int ROIIndex, int shapeIndex);

	Integer getRectangleTheT(int ROIIndex, int shapeIndex);

	Integer getRectangleTheZ(int ROIIndex, int shapeIndex);

	String getRectangleTransform(int ROIIndex, int shapeIndex);

	Double getRectangleHeight(int ROIIndex, int shapeIndex);

	Double getRectangleWidth(int ROIIndex, int shapeIndex);

	Double getRectangleX(int ROIIndex, int shapeIndex);

	Double getRectangleY(int ROIIndex, int shapeIndex);

	//
	// Screen property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getScreenAnnotationRef(int screenIndex, int annotationRefIndex);

	String getScreenDescription(int screenIndex);

	String getScreenID(int screenIndex);

	String getScreenName(int screenIndex);

	String getScreenPlateRef(int screenIndex, int plateRefIndex);

	String getScreenProtocolDescription(int screenIndex);

	String getScreenProtocolIdentifier(int screenIndex);

	String getScreenReagent(int screenIndex, int reagentIndex);

	String getScreenReagentSetDescription(int screenIndex);

	String getScreenReagentSetIdentifier(int screenIndex);

	String getScreenType(int screenIndex);

	//
	// ScreenRef property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ScreenRef

	//
	// StageLabel property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	String getStageLabelName(int imageIndex);

	Double getStageLabelX(int imageIndex);

	Double getStageLabelY(int imageIndex);

	Double getStageLabelZ(int imageIndex);

	//
	// StringAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getStringAnnotationID(int stringAnnotationIndex);

	String getStringAnnotationNamespace(int stringAnnotationIndex);

	String getStringAnnotationValue(int stringAnnotationIndex);

	//
	// StructuredAnnotations property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getStructuredAnnotationsBooleanAnnotation(int booleanAnnotationIndex);

	String getStructuredAnnotationsDoubleAnnotation(int doubleAnnotationIndex);

	String getStructuredAnnotationsFileAnnotation(int fileAnnotationIndex);

	String getStructuredAnnotationsListAnnotation(int listAnnotationIndex);

	String getStructuredAnnotationsLongAnnotation(int longAnnotationIndex);

	String getStructuredAnnotationsStringAnnotation(int stringAnnotationIndex);

	String getStructuredAnnotationsTimestampAnnotation(int timestampAnnotationIndex);

	String getStructuredAnnotationsXMLAnnotation(int XMLAnnotationIndex);

	//
	// Text property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	String getTextDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	Integer getTextFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	Integer getTextFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	String getTextID(int ROIIndex, int shapeIndex);

	String getTextLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	String getTextName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	Integer getTextStroke(int ROIIndex, int shapeIndex);

	String getTextStrokeDashArray(int ROIIndex, int shapeIndex);

	Double getTextStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	Integer getTextTheC(int ROIIndex, int shapeIndex);

	Integer getTextTheT(int ROIIndex, int shapeIndex);

	Integer getTextTheZ(int ROIIndex, int shapeIndex);

	String getTextTransform(int ROIIndex, int shapeIndex);

	String getTextValue(int ROIIndex, int shapeIndex);

	Double getTextX(int ROIIndex, int shapeIndex);

	Double getTextY(int ROIIndex, int shapeIndex);

	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	Integer getTiffDataFirstC(int imageIndex, int tiffDataIndex);

	Integer getTiffDataFirstT(int imageIndex, int tiffDataIndex);

	Integer getTiffDataFirstZ(int imageIndex, int tiffDataIndex);

	Integer getTiffDataIFD(int imageIndex, int tiffDataIndex);

	Integer getTiffDataPlaneCount(int imageIndex, int tiffDataIndex);

	String getTiffDataUUID(int imageIndex, int tiffDataIndex);

	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTimestampAnnotationID(int timestampAnnotationIndex);

	String getTimestampAnnotationNamespace(int timestampAnnotationIndex);

	String getTimestampAnnotationValue(int timestampAnnotationIndex);

	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	Integer getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);

	Integer getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);

	Integer getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);

	Integer getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);

	String getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);

	//
	// UUID property storage
	//
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	String getUUIDFileName(int imageIndex, int tiffDataIndex);

	//
	// Union property storage
	//
	// {u'ROI': {u'OME': None}}
	// Is multi path? False

	String getUnionShape(int ROIIndex, int shapeIndex);

	//
	// Well property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	String getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex);

	Integer getWellColor(int plateIndex, int wellIndex);

	NonNegativeInteger getWellColumn(int plateIndex, int wellIndex);

	String getWellExternalDescription(int plateIndex, int wellIndex);

	String getWellExternalIdentifier(int plateIndex, int wellIndex);

	String getWellID(int plateIndex, int wellIndex);

	String getWellReagentRef(int plateIndex, int wellIndex);

	NonNegativeInteger getWellRow(int plateIndex, int wellIndex);

	String getWellStatus(int plateIndex, int wellIndex);

	String getWellWellSample(int plateIndex, int wellIndex, int wellSampleIndex);

	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex);

	String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

	String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);

	NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

	Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex);

	Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex);

	Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

	//
	// WellSampleRef property storage
	//
	// {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference WellSampleRef

	//
	// XMLAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getXMLAnnotationID(int XMLAnnotationIndex);

	String getXMLAnnotationNamespace(int XMLAnnotationIndex);

	String getXMLAnnotationValue(int XMLAnnotationIndex);

}
