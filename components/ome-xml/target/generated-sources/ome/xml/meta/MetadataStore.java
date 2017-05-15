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
 * <p>See {@link ome.xml.meta.OMEXMLMetadata} for an example
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
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataStore extends BaseMetadata
{
	void createRoot();

	MetadataRoot getRoot();

	void setRoot(MetadataRoot root);

	// -- Entity storage (manual definitions) --

	void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex);

	void setMaskBinData(byte[] binData, int ROIIndex, int shapeIndex);

	void setMapAnnotationValue(java.util.List<ome.xml.model.MapPair> value, int mapAnnotationIndex);

	void setGenericExcitationSourceMap(java.util.List<ome.xml.model.MapPair> map, int instrumentIndex, int lightSourceIndex);

	void setImagingEnvironmentMap(java.util.List<ome.xml.model.MapPair> map, int imageIndex);

	// -- Entity storage (code generated definitions) --

	/** Sets the UUID associated with this collection of metadata. */
	void setUUID(String uuid);

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
	void setArcAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	void setArcID(String id, int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setArcModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setArcPower(Power power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex);

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	void setBinaryFileFileName(String fileName, int fileAnnotationIndex);

	void setBinaryFileMIMEType(String mimeType, int fileAnnotationIndex);

	void setBinaryFileSize(NonNegativeLong size, int fileAnnotationIndex);

	//
	// BinaryOnly property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setBinaryOnlyMetadataFile(String metadataFile);

	void setBinaryOnlyUUID(String uuid);

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setBooleanAnnotationAnnotationRef(String annotation, int booleanAnnotationIndex, int annotationRefIndex);

	void setBooleanAnnotationAnnotator(String annotator, int booleanAnnotationIndex);

	void setBooleanAnnotationDescription(String description, int booleanAnnotationIndex);

	void setBooleanAnnotationID(String id, int booleanAnnotationIndex);

	void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex);

	void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex);

	//
	// Channel property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex);

	void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex);

	void setChannelColor(Color color, int imageIndex, int channelIndex);

	void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex);

	void setChannelEmissionWavelength(Length emissionWavelength, int imageIndex, int channelIndex);

	void setChannelExcitationWavelength(Length excitationWavelength, int imageIndex, int channelIndex);

	void setChannelFilterSetRef(String filterSet, int imageIndex, int channelIndex);

	void setChannelFluor(String fluor, int imageIndex, int channelIndex);

	void setChannelID(String id, int imageIndex, int channelIndex);

	void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex);

	void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex);

	void setChannelName(String name, int imageIndex, int channelIndex);

	void setChannelPinholeSize(Length pinholeSize, int imageIndex, int channelIndex);

	void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex);

	void setChannelSamplesPerPixel(PositiveInteger samplesPerPixel, int imageIndex, int channelIndex);

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setCommentAnnotationAnnotationRef(String annotation, int commentAnnotationIndex, int annotationRefIndex);

	void setCommentAnnotationAnnotator(String annotator, int commentAnnotationIndex);

	void setCommentAnnotationDescription(String description, int commentAnnotationIndex);

	void setCommentAnnotationID(String id, int commentAnnotationIndex);

	void setCommentAnnotationNamespace(String namespace, int commentAnnotationIndex);

	void setCommentAnnotationValue(String value, int commentAnnotationIndex);

	//
	// Dataset property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setDatasetAnnotationRef(String annotation, int datasetIndex, int annotationRefIndex);

	void setDatasetDescription(String description, int datasetIndex);

	void setDatasetExperimenterGroupRef(String experimenterGroup, int datasetIndex);

	void setDatasetExperimenterRef(String experimenter, int datasetIndex);

	void setDatasetID(String id, int datasetIndex);

	void setDatasetImageRef(String image, int datasetIndex, int imageRefIndex);

	void setDatasetName(String name, int datasetIndex);

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

	void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex);

	void setDetectorAnnotationRef(String annotation, int instrumentIndex, int detectorIndex, int annotationRefIndex);

	void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex);

	void setDetectorID(String id, int instrumentIndex, int detectorIndex);

	void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex);

	void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

	void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

	void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex);

	void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

	void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex);

	void setDetectorVoltage(ElectricPotential voltage, int instrumentIndex, int detectorIndex);

	void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex);

	//
	// DetectorSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex);

	void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex);

	void setDetectorSettingsID(String id, int imageIndex, int channelIndex);

	void setDetectorSettingsIntegration(PositiveInteger integration, int imageIndex, int channelIndex);

	void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex);

	void setDetectorSettingsReadOutRate(Frequency readOutRate, int imageIndex, int channelIndex);

	void setDetectorSettingsVoltage(ElectricPotential voltage, int imageIndex, int channelIndex);

	void setDetectorSettingsZoom(Double zoom, int imageIndex, int channelIndex);

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setDichroicAnnotationRef(String annotation, int instrumentIndex, int dichroicIndex, int annotationRefIndex);

	void setDichroicID(String id, int instrumentIndex, int dichroicIndex);

	void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex);

	void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex);

	void setDichroicModel(String model, int instrumentIndex, int dichroicIndex);

	void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex);

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

	void setDoubleAnnotationAnnotationRef(String annotation, int doubleAnnotationIndex, int annotationRefIndex);

	void setDoubleAnnotationAnnotator(String annotator, int doubleAnnotationIndex);

	void setDoubleAnnotationDescription(String description, int doubleAnnotationIndex);

	void setDoubleAnnotationID(String id, int doubleAnnotationIndex);

	void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex);

	void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex);

	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	void setEllipseAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setEllipseFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setEllipseFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setEllipseFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setEllipseFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setEllipseFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setEllipseID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setEllipseLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setEllipseLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setEllipseStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setEllipseStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setEllipseText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setEllipseTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setEllipseTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setEllipseTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setEllipseTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setEllipseVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setEllipseRadiusX(Double radiusX, int ROIIndex, int shapeIndex);

	void setEllipseRadiusY(Double radiusY, int ROIIndex, int shapeIndex);

	void setEllipseX(Double x, int ROIIndex, int shapeIndex);

	void setEllipseY(Double y, int ROIIndex, int shapeIndex);

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

	void setExperimentDescription(String description, int experimentIndex);

	void setExperimentExperimenterRef(String experimenter, int experimentIndex);

	void setExperimentID(String id, int experimentIndex);

	void setExperimentType(ExperimentType type, int experimentIndex);

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

	void setExperimenterAnnotationRef(String annotation, int experimenterIndex, int annotationRefIndex);

	void setExperimenterEmail(String email, int experimenterIndex);

	void setExperimenterFirstName(String firstName, int experimenterIndex);

	void setExperimenterID(String id, int experimenterIndex);

	void setExperimenterInstitution(String institution, int experimenterIndex);

	void setExperimenterLastName(String lastName, int experimenterIndex);

	void setExperimenterMiddleName(String middleName, int experimenterIndex);

	void setExperimenterUserName(String userName, int experimenterIndex);

	//
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setExperimenterGroupAnnotationRef(String annotation, int experimenterGroupIndex, int annotationRefIndex);

	void setExperimenterGroupDescription(String description, int experimenterGroupIndex);

	void setExperimenterGroupExperimenterRef(String experimenter, int experimenterGroupIndex, int experimenterRefIndex);

	void setExperimenterGroupID(String id, int experimenterGroupIndex);

	void setExperimenterGroupLeader(String leader, int experimenterGroupIndex, int leaderIndex);

	void setExperimenterGroupName(String name, int experimenterGroupIndex);

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
	void setFilamentAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	void setFilamentID(String id, int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setFilamentPower(Power power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex);

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setFileAnnotationAnnotationRef(String annotation, int fileAnnotationIndex, int annotationRefIndex);

	void setFileAnnotationAnnotator(String annotator, int fileAnnotationIndex);

	void setFileAnnotationDescription(String description, int fileAnnotationIndex);

	void setFileAnnotationID(String id, int fileAnnotationIndex);

	void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex);

	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setFilterAnnotationRef(String annotation, int instrumentIndex, int filterIndex, int annotationRefIndex);

	void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex);

	void setFilterID(String id, int instrumentIndex, int filterIndex);

	void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

	void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

	void setFilterModel(String model, int instrumentIndex, int filterIndex);

	void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex);

	void setFilterType(FilterType type, int instrumentIndex, int filterIndex);

	//
	// FilterSet property storage
	//
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
	void setGenericExcitationSourceAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	void setGenericExcitationSourceID(String id, int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	void setGenericExcitationSourceLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setGenericExcitationSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setGenericExcitationSourceModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setGenericExcitationSourcePower(Power power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setGenericExcitationSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	//
	// Image property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setImageAcquisitionDate(Timestamp acquisitionDate, int imageIndex);

	void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex);

	void setImageDescription(String description, int imageIndex);

	void setImageExperimentRef(String experiment, int imageIndex);

	void setImageExperimenterGroupRef(String experimenterGroup, int imageIndex);

	void setImageExperimenterRef(String experimenter, int imageIndex);

	void setImageID(String id, int imageIndex);

	void setImageInstrumentRef(String instrument, int imageIndex);

	void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex);

	void setImageName(String name, int imageIndex);

	void setImageROIRef(String roi, int imageIndex, int ROIRefIndex);

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

	void setImagingEnvironmentAirPressure(Pressure airPressure, int imageIndex);

	void setImagingEnvironmentCO2Percent(PercentFraction co2Percent, int imageIndex);

	void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex);

	void setImagingEnvironmentTemperature(Temperature temperature, int imageIndex);

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setInstrumentAnnotationRef(String annotation, int instrumentIndex, int annotationRefIndex);

	void setInstrumentID(String id, int instrumentIndex);

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
	void setLabelAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setLabelFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setLabelFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setLabelFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setLabelFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setLabelFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setLabelID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setLabelLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setLabelLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setLabelStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setLabelStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setLabelStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setLabelText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setLabelTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setLabelTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setLabelTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setLabelTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setLabelVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setLabelX(Double x, int ROIIndex, int shapeIndex);

	void setLabelY(Double y, int ROIIndex, int shapeIndex);

	//
	// Laser property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	void setLaserAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	void setLaserID(String id, int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setLaserModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setLaserPower(Power power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setLaserSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int lightSourceIndex);

	void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int lightSourceIndex);

	void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex);

	void setLaserPulse(Pulse pulse, int instrumentIndex, int lightSourceIndex);

	void setLaserPump(String pump, int instrumentIndex, int lightSourceIndex);

	void setLaserRepetitionRate(Frequency repetitionRate, int instrumentIndex, int lightSourceIndex);

	void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex);

	void setLaserType(LaserType type, int instrumentIndex, int lightSourceIndex);

	void setLaserWavelength(Length wavelength, int instrumentIndex, int lightSourceIndex);

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
	void setLightEmittingDiodeAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex);

	// ID accessor from parent LightSource
	void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex);

	// LotNumber accessor from parent LightSource
	void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setLightEmittingDiodePower(Power power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	void setLightPathAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex);

	void setLightPathDichroicRef(String dichroic, int imageIndex, int channelIndex);

	void setLightPathEmissionFilterRef(String emissionFilter, int imageIndex, int channelIndex, int emissionFilterRefIndex);

	void setLightPathExcitationFilterRef(String excitationFilter, int imageIndex, int channelIndex, int excitationFilterRefIndex);

	//
	// LightSourceSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	void setChannelLightSourceSettingsAttenuation(PercentFraction attenuation, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	void setChannelLightSourceSettingsID(String id, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	void setChannelLightSourceSettingsWavelength(Length wavelength, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsWavelength(Length wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	void setLineAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setLineFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setLineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setLineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setLineFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setLineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setLineID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setLineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setLineLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setLineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setLineStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setLineText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setLineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setLineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setLineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setLineTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setLineVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setLineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex);

	void setLineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex);

	void setLineX1(Double x1, int ROIIndex, int shapeIndex);

	void setLineX2(Double x2, int ROIIndex, int shapeIndex);

	void setLineY1(Double y1, int ROIIndex, int shapeIndex);

	void setLineY2(Double y2, int ROIIndex, int shapeIndex);

	//
	// ListAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setListAnnotationAnnotationRef(String annotation, int listAnnotationIndex, int annotationRefIndex);

	void setListAnnotationAnnotator(String annotator, int listAnnotationIndex);

	void setListAnnotationDescription(String description, int listAnnotationIndex);

	void setListAnnotationID(String id, int listAnnotationIndex);

	void setListAnnotationNamespace(String namespace, int listAnnotationIndex);

	//
	// LongAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setLongAnnotationAnnotationRef(String annotation, int longAnnotationIndex, int annotationRefIndex);

	void setLongAnnotationAnnotator(String annotator, int longAnnotationIndex);

	void setLongAnnotationDescription(String description, int longAnnotationIndex);

	void setLongAnnotationID(String id, int longAnnotationIndex);

	void setLongAnnotationNamespace(String namespace, int longAnnotationIndex);

	void setLongAnnotationValue(Long value, int longAnnotationIndex);

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

	void setMapAnnotationAnnotationRef(String annotation, int mapAnnotationIndex, int annotationRefIndex);

	void setMapAnnotationAnnotator(String annotator, int mapAnnotationIndex);

	void setMapAnnotationDescription(String description, int mapAnnotationIndex);

	void setMapAnnotationID(String id, int mapAnnotationIndex);

	void setMapAnnotationNamespace(String namespace, int mapAnnotationIndex);

	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	void setMaskAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setMaskFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setMaskFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setMaskFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setMaskFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setMaskFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setMaskID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setMaskLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setMaskLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setMaskStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setMaskStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setMaskText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setMaskTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setMaskTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setMaskTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setMaskTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setMaskVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setMaskHeight(Double height, int ROIIndex, int shapeIndex);

	void setMaskWidth(Double width, int ROIIndex, int shapeIndex);

	void setMaskX(Double x, int ROIIndex, int shapeIndex);

	void setMaskY(Double y, int ROIIndex, int shapeIndex);

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

	void setMicrobeamManipulationDescription(String description, int experimentIndex, int microbeamManipulationIndex);

	void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex);

	void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex);

	void setMicrobeamManipulationROIRef(String roi, int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex);

	void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex);

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

	void setMicroscopeLotNumber(String lotNumber, int instrumentIndex);

	void setMicroscopeManufacturer(String manufacturer, int instrumentIndex);

	void setMicroscopeModel(String model, int instrumentIndex);

	void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex);

	void setMicroscopeType(MicroscopeType type, int instrumentIndex);

	//
	// Objective property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	void setObjectiveAnnotationRef(String annotation, int instrumentIndex, int objectiveIndex, int annotationRefIndex);

	void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex);

	void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex);

	void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

	void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex);

	void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex);

	void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex);

	void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex);

	void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

	void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

	void setObjectiveNominalMagnification(Double nominalMagnification, int instrumentIndex, int objectiveIndex);

	void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

	void setObjectiveWorkingDistance(Length workingDistance, int instrumentIndex, int objectiveIndex);

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex);

	void setObjectiveSettingsID(String id, int imageIndex);

	void setObjectiveSettingsMedium(Medium medium, int imageIndex);

	void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex);

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setPixelsBigEndian(Boolean bigEndian, int imageIndex);

	void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex);

	void setPixelsID(String id, int imageIndex);

	void setPixelsInterleaved(Boolean interleaved, int imageIndex);

	void setPixelsPhysicalSizeX(Length physicalSizeX, int imageIndex);

	void setPixelsPhysicalSizeY(Length physicalSizeY, int imageIndex);

	void setPixelsPhysicalSizeZ(Length physicalSizeZ, int imageIndex);

	void setPixelsSignificantBits(PositiveInteger significantBits, int imageIndex);

	void setPixelsSizeC(PositiveInteger sizeC, int imageIndex);

	void setPixelsSizeT(PositiveInteger sizeT, int imageIndex);

	void setPixelsSizeX(PositiveInteger sizeX, int imageIndex);

	void setPixelsSizeY(PositiveInteger sizeY, int imageIndex);

	void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex);

	void setPixelsTimeIncrement(Time timeIncrement, int imageIndex);

	void setPixelsType(PixelType type, int imageIndex);

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setPlaneAnnotationRef(String annotation, int imageIndex, int planeIndex, int annotationRefIndex);

	void setPlaneDeltaT(Time deltaT, int imageIndex, int planeIndex);

	void setPlaneExposureTime(Time exposureTime, int imageIndex, int planeIndex);

	void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex);

	void setPlanePositionX(Length positionX, int imageIndex, int planeIndex);

	void setPlanePositionY(Length positionY, int imageIndex, int planeIndex);

	void setPlanePositionZ(Length positionZ, int imageIndex, int planeIndex);

	void setPlaneTheC(NonNegativeInteger theC, int imageIndex, int planeIndex);

	void setPlaneTheT(NonNegativeInteger theT, int imageIndex, int planeIndex);

	void setPlaneTheZ(NonNegativeInteger theZ, int imageIndex, int planeIndex);

	//
	// Plate property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setPlateAnnotationRef(String annotation, int plateIndex, int annotationRefIndex);

	void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex);

	void setPlateColumns(PositiveInteger columns, int plateIndex);

	void setPlateDescription(String description, int plateIndex);

	void setPlateExternalIdentifier(String externalIdentifier, int plateIndex);

	void setPlateFieldIndex(NonNegativeInteger fieldIndex, int plateIndex);

	void setPlateID(String id, int plateIndex);

	void setPlateName(String name, int plateIndex);

	void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex);

	void setPlateRows(PositiveInteger rows, int plateIndex);

	void setPlateStatus(String status, int plateIndex);

	void setPlateWellOriginX(Length wellOriginX, int plateIndex);

	void setPlateWellOriginY(Length wellOriginY, int plateIndex);

	//
	// PlateAcquisition property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	void setPlateAcquisitionAnnotationRef(String annotation, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex);

	void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionEndTime(Timestamp endTime, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionMaximumFieldCount(PositiveInteger maximumFieldCount, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionStartTime(Timestamp startTime, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

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
	void setPointAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setPointFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setPointFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setPointFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setPointFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setPointFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setPointID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setPointLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setPointLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setPointStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setPointStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setPointText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setPointTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setPointTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setPointTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setPointTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setPointVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setPointX(Double x, int ROIIndex, int shapeIndex);

	void setPointY(Double y, int ROIIndex, int shapeIndex);

	//
	// Polygon property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	void setPolygonAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setPolygonFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setPolygonFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setPolygonFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setPolygonFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setPolygonFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setPolygonID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setPolygonLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setPolygonLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setPolygonStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setPolygonStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setPolygonStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setPolygonText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setPolygonTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setPolygonTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setPolygonTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setPolygonTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setPolygonVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setPolygonPoints(String points, int ROIIndex, int shapeIndex);

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	void setPolylineAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setPolylineFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setPolylineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setPolylineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setPolylineFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setPolylineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setPolylineID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setPolylineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setPolylineLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setPolylineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setPolylineStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setPolylineText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setPolylineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setPolylineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setPolylineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setPolylineTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setPolylineVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setPolylineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex);

	void setPolylineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex);

	void setPolylinePoints(String points, int ROIIndex, int shapeIndex);

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setProjectAnnotationRef(String annotation, int projectIndex, int annotationRefIndex);

	void setProjectDatasetRef(String dataset, int projectIndex, int datasetRefIndex);

	void setProjectDescription(String description, int projectIndex);

	void setProjectExperimenterGroupRef(String experimenterGroup, int projectIndex);

	void setProjectExperimenterRef(String experimenter, int projectIndex);

	void setProjectID(String id, int projectIndex);

	void setProjectName(String name, int projectIndex);

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

	void setROIAnnotationRef(String annotation, int ROIIndex, int annotationRefIndex);

	void setROIDescription(String description, int ROIIndex);

	void setROIID(String id, int ROIIndex);

	void setROIName(String name, int ROIIndex);

	void setROINamespace(String namespace, int ROIIndex);

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

	void setReagentAnnotationRef(String annotation, int screenIndex, int reagentIndex, int annotationRefIndex);

	void setReagentDescription(String description, int screenIndex, int reagentIndex);

	void setReagentID(String id, int screenIndex, int reagentIndex);

	void setReagentName(String name, int screenIndex, int reagentIndex);

	void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

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
	void setRectangleAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex);

	// FillColor accessor from parent Shape
	void setRectangleFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setRectangleFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setRectangleFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setRectangleFontSize(Length fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setRectangleFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setRectangleID(String id, int ROIIndex, int shapeIndex);

	// LineCap accessor from parent Shape
	void setRectangleLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setRectangleLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// StrokeColor accessor from parent Shape
	void setRectangleStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setRectangleStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex);

	// Text accessor from parent Shape
	void setRectangleText(String text, int ROIIndex, int shapeIndex);

	// TheC accessor from parent Shape
	void setRectangleTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex);

	// TheT accessor from parent Shape
	void setRectangleTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex);

	// TheZ accessor from parent Shape
	void setRectangleTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex);

	// Transform accessor from parent Shape
	void setRectangleTransform(AffineTransform transform, int ROIIndex, int shapeIndex);

	// Visible accessor from parent Shape
	void setRectangleVisible(Boolean visible, int ROIIndex, int shapeIndex);

	void setRectangleHeight(Double height, int ROIIndex, int shapeIndex);

	void setRectangleWidth(Double width, int ROIIndex, int shapeIndex);

	void setRectangleX(Double x, int ROIIndex, int shapeIndex);

	void setRectangleY(Double y, int ROIIndex, int shapeIndex);

	//
	// Rights property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setRightsRightsHeld(String rightsHeld);

	void setRightsRightsHolder(String rightsHolder);

	//
	// Screen property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setScreenAnnotationRef(String annotation, int screenIndex, int annotationRefIndex);

	void setScreenDescription(String description, int screenIndex);

	void setScreenID(String id, int screenIndex);

	void setScreenName(String name, int screenIndex);

	void setScreenPlateRef(String plate, int screenIndex, int plateRefIndex);

	void setScreenProtocolDescription(String protocolDescription, int screenIndex);

	void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex);

	void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

	void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex);

	void setScreenType(String type, int screenIndex);

	//
	// StageLabel property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setStageLabelName(String name, int imageIndex);

	void setStageLabelX(Length x, int imageIndex);

	void setStageLabelY(Length y, int imageIndex);

	void setStageLabelZ(Length z, int imageIndex);

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

	void setTagAnnotationAnnotationRef(String annotation, int tagAnnotationIndex, int annotationRefIndex);

	void setTagAnnotationAnnotator(String annotator, int tagAnnotationIndex);

	void setTagAnnotationDescription(String description, int tagAnnotationIndex);

	void setTagAnnotationID(String id, int tagAnnotationIndex);

	void setTagAnnotationNamespace(String namespace, int tagAnnotationIndex);

	void setTagAnnotationValue(String value, int tagAnnotationIndex);

	//
	// TermAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setTermAnnotationAnnotationRef(String annotation, int termAnnotationIndex, int annotationRefIndex);

	void setTermAnnotationAnnotator(String annotator, int termAnnotationIndex);

	void setTermAnnotationDescription(String description, int termAnnotationIndex);

	void setTermAnnotationID(String id, int termAnnotationIndex);

	void setTermAnnotationNamespace(String namespace, int termAnnotationIndex);

	void setTermAnnotationValue(String value, int termAnnotationIndex);

	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setTiffDataFirstC(NonNegativeInteger firstC, int imageIndex, int tiffDataIndex);

	void setTiffDataFirstT(NonNegativeInteger firstT, int imageIndex, int tiffDataIndex);

	void setTiffDataFirstZ(NonNegativeInteger firstZ, int imageIndex, int tiffDataIndex);

	void setTiffDataIFD(NonNegativeInteger ifd, int imageIndex, int tiffDataIndex);

	void setTiffDataPlaneCount(NonNegativeInteger planeCount, int imageIndex, int tiffDataIndex);

	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setTimestampAnnotationAnnotationRef(String annotation, int timestampAnnotationIndex, int annotationRefIndex);

	void setTimestampAnnotationAnnotator(String annotator, int timestampAnnotationIndex);

	void setTimestampAnnotationDescription(String description, int timestampAnnotationIndex);

	void setTimestampAnnotationID(String id, int timestampAnnotationIndex);

	void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex);

	void setTimestampAnnotationValue(Timestamp value, int timestampAnnotationIndex);

	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	void setTransmittanceRangeCutIn(Length cutIn, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutInTolerance(Length cutInTolerance, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutOut(Length cutOut, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutOutTolerance(Length cutOutTolerance, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex);

	// Element's text data
	// {u'TiffData': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
	void setUUIDValue(String value, int imageIndex, int tiffDataIndex);

	//
	// UUID property storage
	//
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex);

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

	void setWellAnnotationRef(String annotation, int plateIndex, int wellIndex, int annotationRefIndex);

	void setWellColor(Color color, int plateIndex, int wellIndex);

	void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex);

	void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex);

	void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex);

	void setWellID(String id, int plateIndex, int wellIndex);

	void setWellReagentRef(String reagent, int plateIndex, int wellIndex);

	void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex);

	void setWellType(String type, int plateIndex, int wellIndex);

	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSamplePositionX(Length positionX, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSamplePositionY(Length positionY, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleTimepoint(Timestamp timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

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

	void setXMLAnnotationAnnotationRef(String annotation, int XMLAnnotationIndex, int annotationRefIndex);

	void setXMLAnnotationAnnotator(String annotator, int XMLAnnotationIndex);

	void setXMLAnnotationDescription(String description, int XMLAnnotationIndex);

	void setXMLAnnotationID(String id, int XMLAnnotationIndex);

	void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex);

	void setXMLAnnotationValue(String value, int XMLAnnotationIndex);

}
