/*
 * #%L
 * OME-XML Java library for working with OME-XML metadata structures.
 * %%
 * Copyright (C) 2006 - 2014 Open Microscopy Environment:
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
 *
 *-----------------------------------------------------------------------------
 */

package ome.xml.meta;

import ome.xml.model.*;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

import ome.units.quantity.Angle;
import ome.units.quantity.ElectricPotential;
import ome.units.quantity.Frequency;
import ome.units.quantity.Length;
import ome.units.quantity.Power;
import ome.units.quantity.Pressure;
import ome.units.quantity.Temperature;
import ome.units.quantity.Time;
import ome.units.unit.Unit;

import java.util.Map;

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
 * <p>See {@link ome.xml.meta.OMEXMLMetadata} for an example
 * implementation.
 *
 * @author Melissa Linkert linkert at wisc.edu
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataRetrieve extends BaseMetadata {

	// -- Entity counting (manual definitions) --

	int getPixelsBinDataCount(int imageIndex);

  int getBooleanAnnotationAnnotationCount(int booleanAnnotationIndex);

  int getCommentAnnotationAnnotationCount(int commentAnnotationIndex);

  int getDoubleAnnotationAnnotationCount(int doubleAnnotationIndex);

  int getFileAnnotationAnnotationCount(int fileAnnotationIndex);

  int getListAnnotationAnnotationCount(int listAnnotationIndex);

  int getLongAnnotationAnnotationCount(int longAnnotationIndex);

  int getMapAnnotationAnnotationCount(int mapAnnotationIndex);

  int getTagAnnotationAnnotationCount(int tagAnnotationIndex);

  int getTermAnnotationAnnotationCount(int termAnnotationIndex);

  int getTimestampAnnotationAnnotationCount(int timestampAnnotationIndex);

  int getXMLAnnotationAnnotationCount(int xmlAnnotationIndex);

	String getLightSourceType(int instrumentIndex, int lightSourceIndex);

  String getShapeType(int roiIndex, int shapeIndex);

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	int getLightSourceAnnotationRefCount(int instrumentIndex, int lightSourceIndex);

	int getInstrumentAnnotationRefCount(int instrumentIndex);

	int getObjectiveAnnotationRefCount(int instrumentIndex, int objectiveIndex);

	int getDetectorAnnotationRefCount(int instrumentIndex, int detectorIndex);

	int getChannelAnnotationRefCount(int imageIndex, int channelIndex);

	int getPlateAnnotationRefCount(int plateIndex);

	int getExperimenterGroupAnnotationRefCount(int experimenterGroupIndex);

	int getScreenAnnotationRefCount(int screenIndex);

	int getReagentAnnotationRefCount(int screenIndex, int reagentIndex);

	int getPlaneAnnotationRefCount(int imageIndex, int planeIndex);

	int getExperimenterAnnotationRefCount(int experimenterIndex);

	int getDichroicAnnotationRefCount(int instrumentIndex, int dichroicIndex);

	int getWellAnnotationRefCount(int plateIndex, int wellIndex);

	int getFilterAnnotationRefCount(int instrumentIndex, int filterIndex);

	int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex);

	int getROIAnnotationRefCount(int ROIIndex);

	int getProjectAnnotationRefCount(int projectIndex);

	int getLightPathAnnotationRefCount(int imageIndex, int channelIndex);

	int getImageAnnotationRefCount(int imageIndex);

	int getDatasetAnnotationRefCount(int datasetIndex);

	int getShapeAnnotationRefCount(int ROIIndex, int shapeIndex);

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
	// GenericExcitationSource entity counting
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

	// Map entity counting
	// MapAnnotation entity counting
	int getMapAnnotationCount();

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
	// Rights entity counting
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
	// {u'TiffData': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
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

	java.util.List<ome.xml.model.MapPair> getMapAnnotationValue(int mapAnnotationIndex);

	java.util.List<ome.xml.model.MapPair> getGenericExcitationSourceMap(int instrumentIndex, int lightSourceIndex);

	java.util.List<ome.xml.model.MapPair> getImagingEnvironmentMap(int imageIndex);

	/** Gets the UUID associated with this collection of metadata. */
	String getUUID();

	// -- Entity retrieval (code generated definitions) --

	//
	// AnnotationRef property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}, u'Instrument': {u'OME': None}, u'Objective': {u'Instrument': {u'OME': None}}, u'Detector': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Screen': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'Dichroic': {u'Instrument': {u'OME': None}}, u'Well': {u'Plate': {u'OME': None}}, u'Filter': {u'Instrument': {u'OME': None}}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'ROI': {u'OME': None}, u'Project': {u'OME': None}, u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? True

	//
	// Arc property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	String getArcAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	String getArcID(int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	String getArcLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getArcManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getArcModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Power getArcPower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getArcSerialNumber(int instrumentIndex, int lightSourceIndex);

	ArcType getArcType(int instrumentIndex, int lightSourceIndex);

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	String getBinaryFileFileName(int fileAnnotationIndex);

	String getBinaryFileMIMEType(int fileAnnotationIndex);

	NonNegativeLong getBinaryFileSize(int fileAnnotationIndex);

	//
	// BinaryOnly property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getBinaryOnlyMetadataFile();

	String getBinaryOnlyUUID();

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getBooleanAnnotationAnnotationRef(int booleanAnnotationIndex, int annotationRefIndex);

	String getBooleanAnnotationAnnotator(int booleanAnnotationIndex);

	String getBooleanAnnotationDescription(int booleanAnnotationIndex);

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

	Color getChannelColor(int imageIndex, int channelIndex);

	ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex);

	Length getChannelEmissionWavelength(int imageIndex, int channelIndex);

	Length getChannelExcitationWavelength(int imageIndex, int channelIndex);

	String getChannelFilterSetRef(int imageIndex, int channelIndex);

	String getChannelFluor(int imageIndex, int channelIndex);

	String getChannelID(int imageIndex, int channelIndex);

	IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex);

	Double getChannelNDFilter(int imageIndex, int channelIndex);

	String getChannelName(int imageIndex, int channelIndex);

	Length getChannelPinholeSize(int imageIndex, int channelIndex);

	Integer getChannelPockelCellSetting(int imageIndex, int channelIndex);

	PositiveInteger getChannelSamplesPerPixel(int imageIndex, int channelIndex);

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getCommentAnnotationAnnotationRef(int commentAnnotationIndex, int annotationRefIndex);

	String getCommentAnnotationAnnotator(int commentAnnotationIndex);

	String getCommentAnnotationDescription(int commentAnnotationIndex);

	String getCommentAnnotationID(int commentAnnotationIndex);

	String getCommentAnnotationNamespace(int commentAnnotationIndex);

	String getCommentAnnotationValue(int commentAnnotationIndex);

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

	//
	// DatasetRef property storage
	//
	// {u'Project': {u'OME': None}}
	// Is multi path? False

	//
	// Detector property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex);

	String getDetectorAnnotationRef(int instrumentIndex, int detectorIndex, int annotationRefIndex);

	Double getDetectorGain(int instrumentIndex, int detectorIndex);

	String getDetectorID(int instrumentIndex, int detectorIndex);

	String getDetectorLotNumber(int instrumentIndex, int detectorIndex);

	String getDetectorManufacturer(int instrumentIndex, int detectorIndex);

	String getDetectorModel(int instrumentIndex, int detectorIndex);

	Double getDetectorOffset(int instrumentIndex, int detectorIndex);

	String getDetectorSerialNumber(int instrumentIndex, int detectorIndex);

	DetectorType getDetectorType(int instrumentIndex, int detectorIndex);

	ElectricPotential getDetectorVoltage(int instrumentIndex, int detectorIndex);

	Double getDetectorZoom(int instrumentIndex, int detectorIndex);

	//
	// DetectorSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	Binning getDetectorSettingsBinning(int imageIndex, int channelIndex);

	Double getDetectorSettingsGain(int imageIndex, int channelIndex);

	String getDetectorSettingsID(int imageIndex, int channelIndex);

	PositiveInteger getDetectorSettingsIntegration(int imageIndex, int channelIndex);

	Double getDetectorSettingsOffset(int imageIndex, int channelIndex);

	Frequency getDetectorSettingsReadOutRate(int imageIndex, int channelIndex);

	ElectricPotential getDetectorSettingsVoltage(int imageIndex, int channelIndex);

	Double getDetectorSettingsZoom(int imageIndex, int channelIndex);

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getDichroicAnnotationRef(int instrumentIndex, int dichroicIndex, int annotationRefIndex);

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

	//
	// DoubleAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getDoubleAnnotationAnnotationRef(int doubleAnnotationIndex, int annotationRefIndex);

	String getDoubleAnnotationAnnotator(int doubleAnnotationIndex);

	String getDoubleAnnotationDescription(int doubleAnnotationIndex);

	String getDoubleAnnotationID(int doubleAnnotationIndex);

	String getDoubleAnnotationNamespace(int doubleAnnotationIndex);

	Double getDoubleAnnotationValue(int doubleAnnotationIndex);

	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	String getEllipseAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getEllipseFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getEllipseFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getEllipseFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getEllipseFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getEllipseFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getEllipseID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getEllipseLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getEllipseLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getEllipseStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getEllipseStrokeWidth(int ROIIndex, int shapeIndex);

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

	ExperimentType getExperimentType(int experimentIndex);

	//
	// ExperimentRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	//
	// Experimenter property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex);

	String getExperimenterEmail(int experimenterIndex);

	String getExperimenterFirstName(int experimenterIndex);

	String getExperimenterID(int experimenterIndex);

	String getExperimenterInstitution(int experimenterIndex);

	String getExperimenterLastName(int experimenterIndex);

	String getExperimenterMiddleName(int experimenterIndex);

	String getExperimenterUserName(int experimenterIndex);

	//
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getExperimenterGroupAnnotationRef(int experimenterGroupIndex, int annotationRefIndex);

	String getExperimenterGroupDescription(int experimenterGroupIndex);

	String getExperimenterGroupExperimenterRef(int experimenterGroupIndex, int experimenterRefIndex);

	String getExperimenterGroupID(int experimenterGroupIndex);

	String getExperimenterGroupLeader(int experimenterGroupIndex, int leaderIndex);

	String getExperimenterGroupName(int experimenterGroupIndex);

	//
	// ExperimenterGroupRef property storage
	//
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	//
	// ExperimenterRef property storage
	//
	// {u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	//
	// Filament property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	String getFilamentAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	String getFilamentID(int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getFilamentModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Power getFilamentPower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex);

	FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex);

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getFileAnnotationAnnotationRef(int fileAnnotationIndex, int annotationRefIndex);

	String getFileAnnotationAnnotator(int fileAnnotationIndex);

	String getFileAnnotationDescription(int fileAnnotationIndex);

	String getFileAnnotationID(int fileAnnotationIndex);

	String getFileAnnotationNamespace(int fileAnnotationIndex);

	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	String getFilterAnnotationRef(int instrumentIndex, int filterIndex, int annotationRefIndex);

	String getFilterFilterWheel(int instrumentIndex, int filterIndex);

	String getFilterID(int instrumentIndex, int filterIndex);

	String getFilterLotNumber(int instrumentIndex, int filterIndex);

	String getFilterManufacturer(int instrumentIndex, int filterIndex);

	String getFilterModel(int instrumentIndex, int filterIndex);

	String getFilterSerialNumber(int instrumentIndex, int filterIndex);

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
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	//
	// GenericExcitationSource property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	String getGenericExcitationSourceAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	String getGenericExcitationSourceID(int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	String getGenericExcitationSourceLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getGenericExcitationSourceManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getGenericExcitationSourceModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Power getGenericExcitationSourcePower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getGenericExcitationSourceSerialNumber(int instrumentIndex, int lightSourceIndex);

	//
	// Image property storage
	//
	// {u'OME': None}
	// Is multi path? False

	Timestamp getImageAcquisitionDate(int imageIndex);

	String getImageAnnotationRef(int imageIndex, int annotationRefIndex);

	String getImageDescription(int imageIndex);

	String getImageExperimentRef(int imageIndex);

	String getImageExperimenterGroupRef(int imageIndex);

	String getImageExperimenterRef(int imageIndex);

	String getImageID(int imageIndex);

	String getImageInstrumentRef(int imageIndex);

	String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex);

	String getImageName(int imageIndex);

	String getImageROIRef(int imageIndex, int ROIRefIndex);

	//
	// ImageRef property storage
	//
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	//
	// ImagingEnvironment property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	Pressure getImagingEnvironmentAirPressure(int imageIndex);

	PercentFraction getImagingEnvironmentCO2Percent(int imageIndex);

	PercentFraction getImagingEnvironmentHumidity(int imageIndex);

	Temperature getImagingEnvironmentTemperature(int imageIndex);

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getInstrumentAnnotationRef(int instrumentIndex, int annotationRefIndex);

	String getInstrumentID(int instrumentIndex);

	//
	// InstrumentRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	//
	// Label property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	String getLabelAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getLabelFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getLabelFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getLabelFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getLabelFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getLabelFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getLabelID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getLabelLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getLabelLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getLabelStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getLabelStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getLabelStrokeWidth(int ROIIndex, int shapeIndex);

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

	// AnnotationRef accessor from parent LightSource
	String getLaserAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	String getLaserID(int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	String getLaserLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getLaserManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getLaserModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Power getLaserPower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getLaserSerialNumber(int instrumentIndex, int lightSourceIndex);

	PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex);

	LaserMedium getLaserLaserMedium(int instrumentIndex, int lightSourceIndex);

	Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex);

	Pulse getLaserPulse(int instrumentIndex, int lightSourceIndex);

	String getLaserPump(int instrumentIndex, int lightSourceIndex);

	Frequency getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex);

	Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex);

	LaserType getLaserType(int instrumentIndex, int lightSourceIndex);

	Length getLaserWavelength(int instrumentIndex, int lightSourceIndex);

	//
	// Leader property storage
	//
	// {u'ExperimenterGroup': {u'OME': None}}
	// Is multi path? False

	//
	// LightEmittingDiode property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	String getLightEmittingDiodeAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	String getLightEmittingDiodeID(int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	Power getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightSourceIndex);

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	String getLightPathAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex);

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

	Length getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex);

	Length getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	String getLineAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getLineFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getLineFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getLineFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getLineFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getLineFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getLineID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getLineLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getLineLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getLineStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getLineStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getLineStrokeWidth(int ROIIndex, int shapeIndex);

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

	String getListAnnotationAnnotator(int listAnnotationIndex);

	String getListAnnotationDescription(int listAnnotationIndex);

	String getListAnnotationID(int listAnnotationIndex);

	String getListAnnotationNamespace(int listAnnotationIndex);

	//
	// LongAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getLongAnnotationAnnotationRef(int longAnnotationIndex, int annotationRefIndex);

	String getLongAnnotationAnnotator(int longAnnotationIndex);

	String getLongAnnotationDescription(int longAnnotationIndex);

	String getLongAnnotationID(int longAnnotationIndex);

	String getLongAnnotationNamespace(int longAnnotationIndex);

	Long getLongAnnotationValue(int longAnnotationIndex);

	//
	// Map property storage
	//
	// {u'GenericExcitationSource': {u'LightSource': {u'Instrument': {u'OME': None}}}, u'ImagingEnvironment': {u'Image': {u'OME': None}}}
	// Is multi path? True

	//
	// MapAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getMapAnnotationAnnotationRef(int mapAnnotationIndex, int annotationRefIndex);

	String getMapAnnotationAnnotator(int mapAnnotationIndex);

	String getMapAnnotationDescription(int mapAnnotationIndex);

	String getMapAnnotationID(int mapAnnotationIndex);

	String getMapAnnotationNamespace(int mapAnnotationIndex);

	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	String getMaskAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getMaskFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getMaskFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getMaskFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getMaskFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getMaskFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getMaskID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getMaskLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getMaskLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getMaskStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getMaskStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getMaskStrokeWidth(int ROIIndex, int shapeIndex);

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

	String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex);

	MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex);

	//
	// MicrobeamManipulationRef property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

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

	String getObjectiveAnnotationRef(int instrumentIndex, int objectiveIndex, int annotationRefIndex);

	Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex);

	Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex);

	String getObjectiveID(int instrumentIndex, int objectiveIndex);

	Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex);

	Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex);

	Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex);

	String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex);

	String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex);

	String getObjectiveModel(int instrumentIndex, int objectiveIndex);

	Double getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex);

	String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex);

	Length getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex);

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	Double getObjectiveSettingsCorrectionCollar(int imageIndex);

	String getObjectiveSettingsID(int imageIndex);

	Medium getObjectiveSettingsMedium(int imageIndex);

	Double getObjectiveSettingsRefractiveIndex(int imageIndex);

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	Boolean getPixelsBigEndian(int imageIndex);

	DimensionOrder getPixelsDimensionOrder(int imageIndex);

	String getPixelsID(int imageIndex);

	Boolean getPixelsInterleaved(int imageIndex);

	Length getPixelsPhysicalSizeX(int imageIndex);

	Length getPixelsPhysicalSizeY(int imageIndex);

	Length getPixelsPhysicalSizeZ(int imageIndex);

	PositiveInteger getPixelsSignificantBits(int imageIndex);

	PositiveInteger getPixelsSizeC(int imageIndex);

	PositiveInteger getPixelsSizeT(int imageIndex);

	PositiveInteger getPixelsSizeX(int imageIndex);

	PositiveInteger getPixelsSizeY(int imageIndex);

	PositiveInteger getPixelsSizeZ(int imageIndex);

	Time getPixelsTimeIncrement(int imageIndex);

	PixelType getPixelsType(int imageIndex);

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex);

	Time getPlaneDeltaT(int imageIndex, int planeIndex);

	Time getPlaneExposureTime(int imageIndex, int planeIndex);

	String getPlaneHashSHA1(int imageIndex, int planeIndex);

	Length getPlanePositionX(int imageIndex, int planeIndex);

	Length getPlanePositionY(int imageIndex, int planeIndex);

	Length getPlanePositionZ(int imageIndex, int planeIndex);

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

	NamingConvention getPlateRowNamingConvention(int plateIndex);

	PositiveInteger getPlateRows(int plateIndex);

	String getPlateStatus(int plateIndex);

	Length getPlateWellOriginX(int plateIndex);

	Length getPlateWellOriginY(int plateIndex);

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

	Timestamp getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex);

	String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

	//
	// PlateRef property storage
	//
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	//
	// Point property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	String getPointAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getPointFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getPointFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getPointFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getPointFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getPointFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getPointID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getPointLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getPointLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getPointStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPointStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getPointStrokeWidth(int ROIIndex, int shapeIndex);

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

	// AnnotationRef accessor from parent Shape
	String getPolygonAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getPolygonFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getPolygonFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getPolygonFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getPolygonFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getPolygonFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getPolygonID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getPolygonLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getPolygonLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getPolygonStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPolygonStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getPolygonStrokeWidth(int ROIIndex, int shapeIndex);

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

	// AnnotationRef accessor from parent Shape
	String getPolylineAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getPolylineFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getPolylineFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getPolylineFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getPolylineFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getPolylineFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getPolylineID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getPolylineLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getPolylineLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getPolylineStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getPolylineStrokeWidth(int ROIIndex, int shapeIndex);

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

	//
	// ROIRef property storage
	//
	// {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

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

	//
	// Rectangle property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	String getRectangleAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	Color getRectangleFillColor(int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	FillRule getRectangleFillRule(int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	FontFamily getRectangleFontFamily(int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	Length getRectangleFontSize(int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	FontStyle getRectangleFontStyle(int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	String getRectangleID(int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	LineCap getRectangleLineCap(int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	Boolean getRectangleLocked(int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	Color getRectangleStrokeColor(int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	Length getRectangleStrokeWidth(int ROIIndex, int shapeIndex);

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
	// Rights property storage
	//
	// {u'OME': None}
	// Is multi path? False

	String getRightsRightsHeld();

	String getRightsRightsHolder();

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

	String getScreenReagentSetDescription(int screenIndex);

	String getScreenReagentSetIdentifier(int screenIndex);

	String getScreenType(int screenIndex);

	//
	// StageLabel property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	String getStageLabelName(int imageIndex);

	Length getStageLabelX(int imageIndex);

	Length getStageLabelY(int imageIndex);

	Length getStageLabelZ(int imageIndex);

	//
	// StructuredAnnotations property storage
	//
	// {u'OME': None}
	// Is multi path? False

	//
	// TagAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTagAnnotationAnnotationRef(int tagAnnotationIndex, int annotationRefIndex);

	String getTagAnnotationAnnotator(int tagAnnotationIndex);

	String getTagAnnotationDescription(int tagAnnotationIndex);

	String getTagAnnotationID(int tagAnnotationIndex);

	String getTagAnnotationNamespace(int tagAnnotationIndex);

	String getTagAnnotationValue(int tagAnnotationIndex);

	//
	// TermAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTermAnnotationAnnotationRef(int termAnnotationIndex, int annotationRefIndex);

	String getTermAnnotationAnnotator(int termAnnotationIndex);

	String getTermAnnotationDescription(int termAnnotationIndex);

	String getTermAnnotationID(int termAnnotationIndex);

	String getTermAnnotationNamespace(int termAnnotationIndex);

	String getTermAnnotationValue(int termAnnotationIndex);

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

	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getTimestampAnnotationAnnotationRef(int timestampAnnotationIndex, int annotationRefIndex);

	String getTimestampAnnotationAnnotator(int timestampAnnotationIndex);

	String getTimestampAnnotationDescription(int timestampAnnotationIndex);

	String getTimestampAnnotationID(int timestampAnnotationIndex);

	String getTimestampAnnotationNamespace(int timestampAnnotationIndex);

	Timestamp getTimestampAnnotationValue(int timestampAnnotationIndex);

	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	Length getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex);

	Length getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex);

	Length getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex);

	Length getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex);

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

	String getWellReagentRef(int plateIndex, int wellIndex);

	NonNegativeInteger getWellRow(int plateIndex, int wellIndex);

	String getWellType(int plateIndex, int wellIndex);

	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex);

	String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex);

	NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex);

	Length getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex);

	Length getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex);

	Timestamp getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex);

	//
	// WellSampleRef property storage
	//
	// {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	//
	// XMLAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	String getXMLAnnotationAnnotationRef(int XMLAnnotationIndex, int annotationRefIndex);

	String getXMLAnnotationAnnotator(int XMLAnnotationIndex);

	String getXMLAnnotationDescription(int XMLAnnotationIndex);

	String getXMLAnnotationID(int XMLAnnotationIndex);

	String getXMLAnnotationNamespace(int XMLAnnotationIndex);

	String getXMLAnnotationValue(int XMLAnnotationIndex);

}
