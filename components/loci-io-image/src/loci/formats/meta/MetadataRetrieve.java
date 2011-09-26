//
// MetadataRetrieve.java
//

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
 * Created by melissa via xsd-fu on 2011-06-09 09:58:16.318560
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/meta/MetadataRetrieve.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/meta/MetadataRetrieve.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataRetrieve {

	// -- Entity counting (manual definitions) --

	int getPixelsBinDataCount(int imageIndex);

	String getLightSourceType(int instrumentIndex, int lightSourceIndex);

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	int getROIAnnotationRefCount(int ROIIndex);

	int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex);

	int getPlateAnnotationRefCount(int plateIndex);

	int getImageAnnotationRefCount(int imageIndex);

	int getScreenAnnotationRefCount(int screenIndex);

	int getWellAnnotationRefCount(int plateIndex, int wellIndex);

	int getDatasetAnnotationRefCount(int datasetIndex);

	int getProjectAnnotationRefCount(int projectIndex);

	int getReagentAnnotationRefCount(int screenIndex, int reagentIndex);

	int getPlaneAnnotationRefCount(int imageIndex, int planeIndex);

	int getExperimenterAnnotationRefCount(int experimenterIndex);

	int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex);

	int getPixelsAnnotationRefCount(int imageIndex);

	int getChannelAnnotationRefCount(int imageIndex, int channelIndex);

	// Arc entity counting
	// BinaryFile entity counting
	// BinaryOnly entity counting
	// BooleanAnnotation entity counting
	int getBooleanAnnotationCount();

	// Channel entity counting
	int getChannelCount(int imageIndex);

	// CommentAnnotation entity counting
	int getCommentAnnotationCount();

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

	// ImageRef entity counting
	// ImagingEnvironment entity counting
	// Instrument entity counting
	int getInstrumentCount();

	// InstrumentRef entity counting
	// Laser entity counting
	// Leader entity counting
	// LightEmittingDiode entity counting
	// LightPath entity counting
	// LightSource entity counting
	int getLightSourceCount(int instrumentIndex);

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

	// Shape entity counting
	int getShapeCount(int ROIIndex);

	// StageLabel entity counting
	// StructuredAnnotations entity counting
	// TagAnnotation entity counting
	int getTagAnnotationCount();

	// TermAnnotation entity counting
	int getTermAnnotationCount();

	// Text entity counting
	// TiffData entity counting
	int getTiffDataCount(int imageIndex);

	// TimestampAnnotation entity counting
	int getTimestampAnnotationCount();

	// TransmittanceRange entity counting
	// Element's text data
	// {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	String getUUIDValue(int imageIndex, int tiffDataIndex);

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


	// -- Entity retrieval (manual definitions) --

	Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex);

	// -- Entity retrieval (code generated definitions) --

	/** Gets the UUID associated with this collection of metadata. */
	String getUUID();

	//
	// AnnotationRef property storage
	//
	// {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
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
	// ID accessor from parent LightSource
	String getArcID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	String getArcLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getArcManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getArcModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Double getArcPower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getArcSerialNumber(int instrumentIndex, int lightSourceIndex);

	ArcType getArcType(int instrumentIndex, int lightSourceIndex);

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex);

	String getOTFBinaryFileFileName(int instrumentIndex, int OTFIndex);

	String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex);

	String getOTFBinaryFileMIMEType(int instrumentIndex, int OTFIndex);

	NonNegativeLong getFileAnnotationBinaryFileSize(int fileAnnotationIndex);

	NonNegativeLong getOTFBinaryFileSize(int instrumentIndex, int OTFIndex);

	//
	// BinaryOnly property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getBinaryOnlyMetadataFile(int metadataFileIndex);

	String getBinaryOnlyUUID(int UUIDIndex);

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getBooleanAnnotationAnnotationRef(int booleanAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getBooleanAnnotationDescription(int booleanAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getBooleanAnnotationID(int booleanAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getBooleanAnnotationNamespace(int booleanAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	Boolean getBooleanAnnotationValue(int booleanAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Channel property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex);

	String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex);

	Integer getChannelColor(int imageIndex, int channelIndex);

	ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex);

	// Ignoring DetectorSettings element, complex property
	PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex);

	PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex);

	String getChannelFilterSetRef(int imageIndex, int channelIndex);

	String getChannelFluor(int imageIndex, int channelIndex);

	String getChannelID(int imageIndex, int channelIndex);

	IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex);

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	Double getChannelNDFilter(int imageIndex, int channelIndex);

	String getChannelName(int imageIndex, int channelIndex);

	String getChannelOTFRef(int imageIndex, int channelIndex);

	Double getChannelPinholeSize(int imageIndex, int channelIndex);

	Integer getChannelPockelCellSetting(int imageIndex, int channelIndex);

	PositiveInteger getChannelSamplesPerPixel(int imageIndex, int channelIndex);

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getCommentAnnotationAnnotationRef(int commentAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getCommentAnnotationDescription(int commentAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getCommentAnnotationID(int commentAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getCommentAnnotationNamespace(int commentAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	String getCommentAnnotationValue(int commentAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
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

	// Ignoring Image_BackReference back reference
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
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	Binning getDetectorSettingsBinning(int imageIndex, int channelIndex);

	Double getDetectorSettingsGain(int imageIndex, int channelIndex);

	String getDetectorSettingsID(int imageIndex, int channelIndex);

	Double getDetectorSettingsOffset(int imageIndex, int channelIndex);

	Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex);

	Double getDetectorSettingsVoltage(int imageIndex, int channelIndex);

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	String getDichroicID(int instrumentIndex, int dichroicIndex);

	// Ignoring LightPath_BackReference back reference
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

	String getDoubleAnnotationAnnotationRef(int doubleAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getDoubleAnnotationDescription(int doubleAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getDoubleAnnotationID(int doubleAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getDoubleAnnotationNamespace(int doubleAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	Double getDoubleAnnotationValue(int doubleAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	String getEllipseDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getEllipseFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getEllipseFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getEllipseID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getEllipseLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getEllipseName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getEllipseStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getEllipseTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getEllipseTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getEllipseTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
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

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
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

	// Ignoring Dataset_BackReference back reference
	String getExperimenterDisplayName(int experimenterIndex);

	String getExperimenterEmail(int experimenterIndex);

	// Ignoring Experiment_BackReference back reference
	String getExperimenterFirstName(int experimenterIndex);

	String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex);

	String getExperimenterID(int experimenterIndex);

	// Ignoring Image_BackReference back reference
	String getExperimenterInstitution(int experimenterIndex);

	String getExperimenterLastName(int experimenterIndex);

	// Ignoring MicrobeamManipulation_BackReference back reference
	String getExperimenterMiddleName(int experimenterIndex);

	// Ignoring Project_BackReference back reference
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
	// Filament property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	String getFilamentID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getFilamentModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Double getFilamentPower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex);

	FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex);

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getFileAnnotationAnnotationRef(int fileAnnotationIndex, int annotationRefIndex);

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getFileAnnotationDescription(int fileAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getFileAnnotationID(int fileAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getFileAnnotationNamespace(int fileAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	String getFilterFilterWheel(int instrumentIndex, int filterIndex);

	String getFilterID(int instrumentIndex, int filterIndex);

	// Ignoring LightPath_BackReference back reference
	String getFilterLotNumber(int instrumentIndex, int filterIndex);

	String getFilterManufacturer(int instrumentIndex, int filterIndex);

	String getFilterModel(int instrumentIndex, int filterIndex);

	String getFilterSerialNumber(int instrumentIndex, int filterIndex);

	// Ignoring TransmittanceRange element, complex property
	FilterType getFilterType(int instrumentIndex, int filterIndex);

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex);

	String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex);

	String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex);

	String getFilterSetID(int instrumentIndex, int filterSetIndex);

	String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);

	String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);

	String getFilterSetModel(int instrumentIndex, int filterSetIndex);

	// Ignoring OTF_BackReference back reference
	String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex);

	//
	// FilterSetRef property storage
	//
	// {u'OTF': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
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

	// Ignoring Dataset_BackReference back reference
	String getGroupDescription(int groupIndex);

	// Ignoring Experimenter_BackReference back reference
	String getGroupID(int groupIndex);

	// Ignoring Image_BackReference back reference
	String getGroupLeader(int groupIndex);

	String getGroupName(int groupIndex);

	// Ignoring Project_BackReference back reference
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

	// Ignoring ImagingEnvironment element, complex property
	String getImageInstrumentRef(int imageIndex);

	String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex);

	String getImageName(int imageIndex);

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	String getImageROIRef(int imageIndex, int ROIRefIndex);

	// Ignoring StageLabel element, complex property
	// Ignoring WellSample_BackReference back reference
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

	PercentFraction getImagingEnvironmentCO2Percent(int imageIndex);

	PercentFraction getImagingEnvironmentHumidity(int imageIndex);

	Double getImagingEnvironmentTemperature(int imageIndex);

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	// Ignoring Detector element, complex property
	// Ignoring Dichroic element, complex property
	// Ignoring Filter element, complex property
	// Ignoring FilterSet element, complex property
	String getInstrumentID(int instrumentIndex);

	// Ignoring Image_BackReference back reference
	// Ignoring LightSource element, complex property
	// Ignoring Microscope element, complex property
	// Ignoring OTF element, complex property
	// Ignoring Objective element, complex property
	//
	// InstrumentRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference InstrumentRef

	//
	// Laser property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	String getLaserID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	String getLaserLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getLaserManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getLaserModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Double getLaserPower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
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
	// ID accessor from parent LightSource
	String getLightEmittingDiodeID(int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Double getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
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
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex);

	PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	String getChannelLightSourceSettingsID(int imageIndex, int channelIndex);

	String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex);

	PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	String getLineDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getLineFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getLineFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getLineID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getLineLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getLineName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getLineStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getLineStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getLineStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getLineTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getLineTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getLineTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
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

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getListAnnotationDescription(int listAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getListAnnotationID(int listAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getListAnnotationNamespace(int listAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// LongAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getLongAnnotationAnnotationRef(int longAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getLongAnnotationDescription(int longAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getLongAnnotationID(int longAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getLongAnnotationNamespace(int longAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	Long getLongAnnotationValue(int longAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	String getMaskDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getMaskFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getMaskFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getMaskID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getMaskLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getMaskName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getMaskStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getMaskStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getMaskStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getMaskTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getMaskTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getMaskTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	String getMaskTransform(int ROIIndex, int shapeIndex);

	// Ignoring BinData element, complex property
	Double getMaskHeight(int ROIIndex, int shapeIndex);

	Double getMaskWidth(int ROIIndex, int shapeIndex);

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

	String getMicrobeamManipulationDescription(int experimentIndex, int microbeamManipulationIndex);

	String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex);

	String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex);

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
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

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	String getOTFFilterSetRef(int instrumentIndex, int OTFIndex);

	String getOTFID(int instrumentIndex, int OTFIndex);

	// Ignoring ObjectiveSettings element, complex property
	Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int OTFIndex);

	PositiveInteger getOTFSizeX(int instrumentIndex, int OTFIndex);

	PositiveInteger getOTFSizeY(int instrumentIndex, int OTFIndex);

	PixelType getOTFType(int instrumentIndex, int OTFIndex);

	//
	// OTFRef property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
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

	PositiveInteger getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

	String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

	Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	Double getImageObjectiveSettingsCorrectionCollar(int imageIndex);

	Double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int OTFIndex);

	String getImageObjectiveSettingsID(int imageIndex);

	String getOTFObjectiveSettingsID(int instrumentIndex, int OTFIndex);

	Medium getImageObjectiveSettingsMedium(int imageIndex);

	Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int OTFIndex);

	Double getImageObjectiveSettingsRefractiveIndex(int imageIndex);

	Double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int OTFIndex);

	//
	// Path property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	String getPathDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getPathFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getPathFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getPathID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getPathLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getPathName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getPathStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPathStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getPathStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getPathTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getPathTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getPathTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	String getPathTransform(int ROIIndex, int shapeIndex);

	String getPathDefinition(int ROIIndex, int shapeIndex);

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex);

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	DimensionOrder getPixelsDimensionOrder(int imageIndex);

	String getPixelsID(int imageIndex);

	// Ignoring MetadataOnly element, complex property
	PositiveFloat getPixelsPhysicalSizeX(int imageIndex);

	PositiveFloat getPixelsPhysicalSizeY(int imageIndex);

	PositiveFloat getPixelsPhysicalSizeZ(int imageIndex);

	// Ignoring Plane element, complex property
	PositiveInteger getPixelsSizeC(int imageIndex);

	PositiveInteger getPixelsSizeT(int imageIndex);

	PositiveInteger getPixelsSizeX(int imageIndex);

	PositiveInteger getPixelsSizeY(int imageIndex);

	PositiveInteger getPixelsSizeZ(int imageIndex);

	// Ignoring TiffData element, complex property
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

	NonNegativeInteger getPlaneTheC(int imageIndex, int planeIndex);

	NonNegativeInteger getPlaneTheT(int imageIndex, int planeIndex);

	NonNegativeInteger getPlaneTheZ(int imageIndex, int planeIndex);

	//
	// Plate property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getPlateAnnotationRef(int plateIndex, int annotationRefIndex);

	NamingConvention getPlateColumnNamingConvention(int plateIndex);

	PositiveInteger getPlateColumns(int plateIndex);

	String getPlateDescription(int plateIndex);

	String getPlateExternalIdentifier(int plateIndex);

	String getPlateID(int plateIndex);

	String getPlateName(int plateIndex);

	// Ignoring PlateAcquisition element, complex property
	NamingConvention getPlateRowNamingConvention(int plateIndex);

	PositiveInteger getPlateRows(int plateIndex);

	String getPlateScreenRef(int plateIndex, int screenRefIndex);

	String getPlateStatus(int plateIndex);

	// Ignoring Well element, complex property
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

	PositiveInteger getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex);

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

	// Description accessor from parent Shape
	String getPointDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getPointFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getPointFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getPointID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getPointLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getPointName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getPointStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPointStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getPointStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getPointTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getPointTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getPointTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	String getPointTransform(int ROIIndex, int shapeIndex);

	Double getPointX(int ROIIndex, int shapeIndex);

	Double getPointY(int ROIIndex, int shapeIndex);

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	String getPolylineDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getPolylineFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getPolylineFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getPolylineID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getPolylineLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getPolylineName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getPolylineStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getPolylineTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getPolylineTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getPolylineTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	String getPolylineTransform(int ROIIndex, int shapeIndex);

	Boolean getPolylineClosed(int ROIIndex, int shapeIndex);

	String getPolylinePoints(int ROIIndex, int shapeIndex);

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getProjectAnnotationRef(int projectIndex, int annotationRefIndex);

	// Ignoring Dataset_BackReference back reference
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

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	String getROIName(int ROIIndex);

	String getROINamespace(int ROIIndex);

	// Ignoring Union element, complex property
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

	// Ignoring Well_BackReference back reference
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

	// Description accessor from parent Shape
	String getRectangleDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getRectangleFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getRectangleFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getRectangleID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getRectangleLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getRectangleName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getRectangleStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getRectangleTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getRectangleTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getRectangleTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
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

	// Ignoring Reagent element, complex property
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
	// StructuredAnnotations property storage
	//
	// {u'OME': None}
	// Is multi path? False

	// Ignoring BooleanAnnotation element, complex property
	// Ignoring CommentAnnotation element, complex property
	// Ignoring DoubleAnnotation element, complex property
	// Ignoring FileAnnotation element, complex property
	// Ignoring ListAnnotation element, complex property
	// Ignoring LongAnnotation element, complex property
	// Ignoring TagAnnotation element, complex property
	// Ignoring TermAnnotation element, complex property
	// Ignoring TimestampAnnotation element, complex property
	// Ignoring XMLAnnotation element, complex property
	//
	// TagAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTagAnnotationAnnotationRef(int tagAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getTagAnnotationDescription(int tagAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getTagAnnotationID(int tagAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getTagAnnotationNamespace(int tagAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	String getTagAnnotationValue(int tagAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TermAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTermAnnotationAnnotationRef(int termAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getTermAnnotationDescription(int termAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getTermAnnotationID(int termAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getTermAnnotationNamespace(int termAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	String getTermAnnotationValue(int termAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Text property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	String getTextDescription(int ROIIndex, int shapeIndex);

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	Integer getTextFill(int ROIIndex, int shapeIndex);

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	NonNegativeInteger getTextFontSize(int ROIIndex, int shapeIndex);

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	String getTextID(int ROIIndex, int shapeIndex);

	// Label accessor from parent Shape
	String getTextLabel(int ROIIndex, int shapeIndex);

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	String getTextName(int ROIIndex, int shapeIndex);

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	Integer getTextStroke(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getTextStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getTextStrokeWidth(int ROIIndex, int shapeIndex);

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	NonNegativeInteger getTextTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getTextTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getTextTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	String getTextTransform(int ROIIndex, int shapeIndex);

	String getTextValue(int ROIIndex, int shapeIndex);

	Double getTextX(int ROIIndex, int shapeIndex);

	Double getTextY(int ROIIndex, int shapeIndex);

	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	NonNegativeInteger getTiffDataFirstC(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataFirstT(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataFirstZ(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataIFD(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataPlaneCount(int imageIndex, int tiffDataIndex);

	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTimestampAnnotationAnnotationRef(int timestampAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getTimestampAnnotationDescription(int timestampAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getTimestampAnnotationID(int timestampAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getTimestampAnnotationNamespace(int timestampAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	String getTimestampAnnotationValue(int timestampAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	PositiveInteger getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);

	NonNegativeInteger getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);

	PositiveInteger getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);

	NonNegativeInteger getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);

	PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex);

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

	// Ignoring Shape element, complex property
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

	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex);

	String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

	String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);

	NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

	// Ignoring PlateAcquisition_BackReference back reference
	Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex);

	Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex);

	String getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

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

	String getXMLAnnotationAnnotationRef(int XMLAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	String getXMLAnnotationDescription(int XMLAnnotationIndex);

	// Ignoring Experimenter_BackReference back reference
	String getXMLAnnotationID(int XMLAnnotationIndex);

	// Ignoring Image_BackReference back reference
	String getXMLAnnotationNamespace(int XMLAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	String getXMLAnnotationValue(int XMLAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
