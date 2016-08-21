/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2013 Open Microscopy Environment:
 *   - Massachusetts Institute of Technology
 *   - National Institutes of Health
 *   - University of Dundee
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:37.542053
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import ome.xml.model.*;
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

  int getBooleanAnnotationAnnotationCount(int booleanAnnotationIndex);

  int getCommentAnnotationAnnotationCount(int commentAnnotationIndex);

  int getDoubleAnnotationAnnotationCount(int doubleAnnotationIndex);

  int getFileAnnotationAnnotationCount(int fileAnnotationIndex);

  int getListAnnotationAnnotationCount(int listAnnotationIndex);

  int getLongAnnotationAnnotationCount(int longAnnotationIndex);

  int getTagAnnotationAnnotationCount(int tagAnnotationIndex);

  int getTermAnnotationAnnotationCount(int termAnnotationIndex);

  int getTimestampAnnotationAnnotationCount(int timestampAnnotationIndex);

  int getXMLAnnotationAnnotationCount(int xmlAnnotationIndex);

	String getLightSourceType(int instrumentIndex, int lightSourceIndex);

  String getShapeType(int roiIndex, int shapeIndex);

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	int getROIAnnotationRefCount(int ROIIndex);

	int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex);

	int getPlateAnnotationRefCount(int plateIndex);

	int getExperimenterGroupAnnotationRefCount(int experimenterGroupIndex);

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

	// Dataset entity counting
	int getDatasetCount();

	// DatasetRef entity counting
	int getDatasetRefCount(int projectIndex);

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

	// ExperimenterGroup entity counting
	int getExperimenterGroupCount();

	// ExperimenterGroupRef entity counting
	// ExperimenterRef entity counting
	int getExperimenterGroupExperimenterRefCount(int experimenterGroupIndex);

	// Filament entity counting
	// FileAnnotation entity counting
	int getFileAnnotationCount();

	// Filter entity counting
	int getFilterCount(int instrumentIndex);

	// FilterSet entity counting
	int getFilterSetCount(int instrumentIndex);

	// FilterSetRef entity counting
	// Image entity counting
	int getImageCount();

	// ImageRef entity counting
	int getDatasetImageRefCount(int datasetIndex);

	// ImagingEnvironment entity counting
	// Instrument entity counting
	int getInstrumentCount();

	// InstrumentRef entity counting
	// Label entity counting
	// Laser entity counting
	// Leader entity counting
	int getLeaderCount(int experimenterGroupIndex);

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
	// Objective entity counting
	int getObjectiveCount(int instrumentIndex);

	// ObjectiveSettings entity counting
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
	// Polygon entity counting
	// Polyline entity counting
	// Project entity counting
	int getProjectCount();

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

	// Shape entity counting
	int getShapeCount(int ROIIndex);

	// StageLabel entity counting
	// StructuredAnnotations entity counting
	// TagAnnotation entity counting
	int getTagAnnotationCount();

	// TermAnnotation entity counting
	int getTermAnnotationCount();

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
	// {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
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
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	String getBinaryFileFileName(int fileAnnotationIndex);

	String getBinaryFileMIMEType(int fileAnnotationIndex);

	NonNegativeLong getBinaryFileSize(int fileAnnotationIndex);

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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
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

	Color getChannelColor(int imageIndex, int channelIndex);

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

	Double getChannelPinholeSize(int imageIndex, int channelIndex);

	// Ignoring Pixels_BackReference back reference
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	String getCommentAnnotationValue(int commentAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Dataset property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex);

	String getDatasetDescription(int datasetIndex);

	String getDatasetExperimenterGroupRef(int datasetIndex);

	String getDatasetExperimenterRef(int datasetIndex);

	String getDatasetID(int datasetIndex);

	String getDatasetImageRef(int datasetIndex, int imageRefIndex);

	String getDatasetName(int datasetIndex);

	// Ignoring Project_BackReference back reference
	//
	// DatasetRef property storage
	//
	// {u'Project': {u'OME': None}}
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

	// Ignoring Instrument_BackReference back reference
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

	// Ignoring DetectorRef back reference
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

	// Ignoring Instrument_BackReference back reference
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	Double getDoubleAnnotationValue(int doubleAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getEllipseFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getEllipseFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getEllipseFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getEllipseFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getEllipseFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getEllipseID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getEllipseLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getEllipseLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getEllipseStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getEllipseText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getEllipseTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getEllipseTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getEllipseTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getEllipseTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getEllipseVisible(int ROIIndex, int shapeIndex);

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
	String getExperimenterEmail(int experimenterIndex);

	// Ignoring Experiment_BackReference back reference
	// Ignoring ExperimenterGroup_BackReference back reference
	String getExperimenterFirstName(int experimenterIndex);

	String getExperimenterID(int experimenterIndex);

	// Ignoring Image_BackReference back reference
	String getExperimenterInstitution(int experimenterIndex);

	String getExperimenterLastName(int experimenterIndex);

	// Ignoring MicrobeamManipulation_BackReference back reference
	String getExperimenterMiddleName(int experimenterIndex);

	// Ignoring Project_BackReference back reference
	String getExperimenterUserName(int experimenterIndex);

	//
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getExperimenterGroupAnnotationRef(int experimenterGroupIndex, int annotationRefIndex);

	// Ignoring Dataset_BackReference back reference
	String getExperimenterGroupDescription(int experimenterGroupIndex);

	String getExperimenterGroupExperimenterRef(int experimenterGroupIndex, int experimenterRefIndex);

	String getExperimenterGroupID(int experimenterGroupIndex);

	// Ignoring Image_BackReference back reference
	String getExperimenterGroupLeader(int experimenterGroupIndex, int leaderIndex);

	String getExperimenterGroupName(int experimenterGroupIndex);

	// Ignoring Project_BackReference back reference
	//
	// ExperimenterGroupRef property storage
	//
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ExperimenterGroupRef

	//
	// ExperimenterRef property storage
	//
	// {u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	// Ignoring FilterSet_BackReference back reference
	String getFilterFilterWheel(int instrumentIndex, int filterIndex);

	String getFilterID(int instrumentIndex, int filterIndex);

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
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

	// Ignoring Instrument_BackReference back reference
	String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex);

	String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex);

	String getFilterSetModel(int instrumentIndex, int filterSetIndex);

	String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex);

	//
	// FilterSetRef property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference FilterSetRef

	//
	// Image property storage
	//
	// {u'OME': None}
	// Is multi path? False

	Timestamp getImageAcquisitionDate(int imageIndex);

	String getImageAnnotationRef(int imageIndex, int annotationRefIndex);

	// Ignoring Dataset_BackReference back reference
	String getImageDescription(int imageIndex);

	String getImageExperimentRef(int imageIndex);

	String getImageExperimenterGroupRef(int imageIndex);

	String getImageExperimenterRef(int imageIndex);

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
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
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
	// Label property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getLabelFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getLabelFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getLabelFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getLabelFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getLabelFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getLabelID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getLabelLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getLabelLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getLabelStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getLabelStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getLabelStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getLabelText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getLabelTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getLabelTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getLabelTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getLabelTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getLabelVisible(int ROIIndex, int shapeIndex);

	Double getLabelX(int ROIIndex, int shapeIndex);

	Double getLabelY(int ROIIndex, int shapeIndex);

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
	// {u'ExperimenterGroup': {u'OME': None}}
	// Is multi path? False

	// 0:9999
	// Is multi path? False
	// Ignoring ExperimenterGroup_BackReference property of reference Leader

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

	// Ignoring LightSourceRef back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex);

	PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getLineFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getLineFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getLineFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getLineFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getLineFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getLineID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getLineLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getLineLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getLineStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getLineStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getLineStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getLineText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getLineTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getLineTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getLineTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getLineTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getLineVisible(int ROIIndex, int shapeIndex);

	Marker getLineMarkerEnd(int ROIIndex, int shapeIndex);

	Marker getLineMarkerStart(int ROIIndex, int shapeIndex);

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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	Long getLongAnnotationValue(int longAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getMaskFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getMaskFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getMaskFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getMaskFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getMaskFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getMaskID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getMaskLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getMaskLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getMaskStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getMaskStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getMaskStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getMaskText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getMaskTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getMaskTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getMaskTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getMaskTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getMaskVisible(int ROIIndex, int shapeIndex);

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

	// Ignoring Experiment_BackReference back reference
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
	// Objective property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

	Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

	String getObjectiveID(int instrumentIndex, int objectiveIndex);

	Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

	// Ignoring Instrument_BackReference back reference
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
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	Double getObjectiveSettingsCorrectionCollar(int imageIndex);

	String getObjectiveSettingsID(int imageIndex);

	Medium getObjectiveSettingsMedium(int imageIndex);

	// Ignoring ObjectiveRef back reference
	Double getObjectiveSettingsRefractiveIndex(int imageIndex);

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

	// Ignoring Pixels_BackReference back reference
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

	NonNegativeInteger getPlateFieldIndex(int plateIndex);

	String getPlateID(int plateIndex);

	String getPlateName(int plateIndex);

	// Ignoring PlateAcquisition element, complex property
	NamingConvention getPlateRowNamingConvention(int plateIndex);

	PositiveInteger getPlateRows(int plateIndex);

	// Ignoring Screen_BackReference back reference
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

	Timestamp getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex);

	PositiveInteger getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex);

	// Ignoring Plate_BackReference back reference
	Timestamp getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex);

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

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getPointFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getPointFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getPointFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getPointFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getPointFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getPointID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getPointLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getPointLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getPointStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPointStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getPointStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getPointText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getPointTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getPointTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getPointTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getPointTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getPointVisible(int ROIIndex, int shapeIndex);

	Double getPointX(int ROIIndex, int shapeIndex);

	Double getPointY(int ROIIndex, int shapeIndex);

	//
	// Polygon property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getPolygonFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getPolygonFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getPolygonFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getPolygonFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getPolygonFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getPolygonID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getPolygonLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getPolygonLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getPolygonStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPolygonStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getPolygonStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getPolygonText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getPolygonTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getPolygonTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getPolygonTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getPolygonTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getPolygonVisible(int ROIIndex, int shapeIndex);

	String getPolygonPoints(int ROIIndex, int shapeIndex);

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getPolylineFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getPolylineFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getPolylineFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getPolylineFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getPolylineFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getPolylineID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getPolylineLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getPolylineLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getPolylineStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getPolylineText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getPolylineTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getPolylineTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getPolylineTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getPolylineTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getPolylineVisible(int ROIIndex, int shapeIndex);

	Marker getPolylineMarkerEnd(int ROIIndex, int shapeIndex);

	Marker getPolylineMarkerStart(int ROIIndex, int shapeIndex);

	String getPolylinePoints(int ROIIndex, int shapeIndex);

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getProjectAnnotationRef(int projectIndex, int annotationRefIndex);

	String getProjectDatasetRef(int projectIndex, int datasetRefIndex);

	String getProjectDescription(int projectIndex);

	String getProjectExperimenterGroupRef(int projectIndex);

	String getProjectExperimenterRef(int projectIndex);

	String getProjectID(int projectIndex);

	String getProjectName(int projectIndex);

	//
	// Pump property storage
	//
	// {u'Laser': {u'LightSource': {u'Instrument': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Pump

	// 0:9999
	// Is multi path? False
	// Ignoring Laser_BackReference property of reference Pump

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

	// Ignoring Screen_BackReference back reference
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

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	Color getRectangleFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getRectangleFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getRectangleFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	NonNegativeInteger getRectangleFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getRectangleFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getRectangleID(int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	LineCap getRectangleLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getRectangleLocked(int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	Color getRectangleStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	String getRectangleText(int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	NonNegativeInteger getRectangleTheC(int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	NonNegativeInteger getRectangleTheT(int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	NonNegativeInteger getRectangleTheZ(int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	AffineTransform getRectangleTransform(int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	Boolean getRectangleVisible(int ROIIndex, int shapeIndex);

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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	String getTermAnnotationValue(int termAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	NonNegativeInteger getTiffDataFirstC(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataFirstT(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataFirstZ(int imageIndex, int tiffDataIndex);

	NonNegativeInteger getTiffDataIFD(int imageIndex, int tiffDataIndex);

	// Ignoring Pixels_BackReference back reference
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	Timestamp getTimestampAnnotationValue(int timestampAnnotationIndex);

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

	Color getWellColor(int plateIndex, int wellIndex);

	NonNegativeInteger getWellColumn(int plateIndex, int wellIndex);

	String getWellExternalDescription(int plateIndex, int wellIndex);

	String getWellExternalIdentifier(int plateIndex, int wellIndex);

	String getWellID(int plateIndex, int wellIndex);

	// Ignoring Plate_BackReference back reference
	String getWellReagentRef(int plateIndex, int wellIndex);

	NonNegativeInteger getWellRow(int plateIndex, int wellIndex);

	String getWellType(int plateIndex, int wellIndex);

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

	Timestamp getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

	// Ignoring Well_BackReference back reference
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

	// Ignoring ExperimenterGroup_BackReference back reference
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
	// Ignoring StructuredAnnotations_BackReference back reference
	String getXMLAnnotationValue(int XMLAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
