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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:37.144670
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import ome.xml.model.*;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

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
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/meta/MetadataStore.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/meta/MetadataStore.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public interface MetadataStore
{
	void createRoot();

	Object getRoot();

	void setRoot(Object root);
	
	// -- Entity storage (manual definitions) --

	void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex);

	void setMaskBinData(byte[] binData, int ROIIndex, int shapeIndex);

	// -- Entity storage (code generated definitions) --

	/** Sets the UUID associated with this collection of metadata. */
	void setUUID(String uuid);

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
	void setArcID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setArcModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setArcPower(Double power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex);

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
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

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setBooleanAnnotationDescription(String description, int booleanAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setBooleanAnnotationID(String id, int booleanAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Channel property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex);

	void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex);

	void setChannelColor(Color color, int imageIndex, int channelIndex);

	void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex);

	// Ignoring DetectorSettings element, complex property
	void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex);

	void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex);

	void setChannelFilterSetRef(String filterSet, int imageIndex, int channelIndex);

	void setChannelFluor(String fluor, int imageIndex, int channelIndex);

	void setChannelID(String id, int imageIndex, int channelIndex);

	void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex);

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex);

	void setChannelName(String name, int imageIndex, int channelIndex);

	void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex);

	// Ignoring Pixels_BackReference back reference
	void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex);

	void setChannelSamplesPerPixel(PositiveInteger samplesPerPixel, int imageIndex, int channelIndex);

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setCommentAnnotationAnnotationRef(String annotation, int commentAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setCommentAnnotationDescription(String description, int commentAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setCommentAnnotationID(String id, int commentAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setCommentAnnotationNamespace(String namespace, int commentAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setCommentAnnotationValue(String value, int commentAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
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

	void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex);

	void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex);

	void setDetectorID(String id, int instrumentIndex, int detectorIndex);

	// Ignoring Instrument_BackReference back reference
	void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex);

	void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex);

	void setDetectorModel(String model, int instrumentIndex, int detectorIndex);

	void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex);

	void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex);

	void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex);

	void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex);

	void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex);

	//
	// DetectorSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex);

	// Ignoring DetectorRef back reference
	void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex);

	void setDetectorSettingsID(String id, int imageIndex, int channelIndex);

	void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex);

	void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex);

	void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex);

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	void setDichroicID(String id, int instrumentIndex, int dichroicIndex);

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex);

	void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex);

	void setDichroicModel(String model, int instrumentIndex, int dichroicIndex);

	void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex);

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

	void setDoubleAnnotationAnnotationRef(String annotation, int doubleAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setDoubleAnnotationDescription(String description, int doubleAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setDoubleAnnotationID(String id, int doubleAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	void setEllipseFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setEllipseFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setEllipseFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setEllipseFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setEllipseFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setEllipseID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setEllipseLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setEllipseLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setEllipseStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setEllipseStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	void setExperimentType(ExperimentType type, int experimentIndex);

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

	void setExperimenterAnnotationRef(String annotation, int experimenterIndex, int annotationRefIndex);

	// Ignoring Dataset_BackReference back reference
	void setExperimenterEmail(String email, int experimenterIndex);

	// Ignoring Experiment_BackReference back reference
	// Ignoring ExperimenterGroup_BackReference back reference
	void setExperimenterFirstName(String firstName, int experimenterIndex);

	void setExperimenterID(String id, int experimenterIndex);

	// Ignoring Image_BackReference back reference
	void setExperimenterInstitution(String institution, int experimenterIndex);

	void setExperimenterLastName(String lastName, int experimenterIndex);

	// Ignoring MicrobeamManipulation_BackReference back reference
	void setExperimenterMiddleName(String middleName, int experimenterIndex);

	// Ignoring Project_BackReference back reference
	void setExperimenterUserName(String userName, int experimenterIndex);

	//
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	void setExperimenterGroupAnnotationRef(String annotation, int experimenterGroupIndex, int annotationRefIndex);

	// Ignoring Dataset_BackReference back reference
	void setExperimenterGroupDescription(String description, int experimenterGroupIndex);

	void setExperimenterGroupExperimenterRef(String experimenter, int experimenterGroupIndex, int experimenterRefIndex);

	void setExperimenterGroupID(String id, int experimenterGroupIndex);

	// Ignoring Image_BackReference back reference
	void setExperimenterGroupLeader(String leader, int experimenterGroupIndex, int leaderIndex);

	void setExperimenterGroupName(String name, int experimenterGroupIndex);

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
	void setFilamentID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setFilamentPower(Double power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex);

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setFileAnnotationAnnotationRef(String annotation, int fileAnnotationIndex, int annotationRefIndex);

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setFileAnnotationDescription(String description, int fileAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setFileAnnotationID(String id, int fileAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex);

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
	void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex);

	void setFilterID(String id, int instrumentIndex, int filterIndex);

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex);

	void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex);

	void setFilterModel(String model, int instrumentIndex, int filterIndex);

	void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex);

	// Ignoring TransmittanceRange element, complex property
	void setFilterType(FilterType type, int instrumentIndex, int filterIndex);

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	void setFilterSetDichroicRef(String dichroic, int instrumentIndex, int filterSetIndex);

	void setFilterSetEmissionFilterRef(String emissionFilter, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex);

	void setFilterSetExcitationFilterRef(String excitationFilter, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex);

	void setFilterSetID(String id, int instrumentIndex, int filterSetIndex);

	// Ignoring Instrument_BackReference back reference
	void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex);

	void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex);

	void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex);

	void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex);

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

	void setImageAcquisitionDate(Timestamp acquisitionDate, int imageIndex);

	void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex);

	// Ignoring Dataset_BackReference back reference
	void setImageDescription(String description, int imageIndex);

	void setImageExperimentRef(String experiment, int imageIndex);

	void setImageExperimenterGroupRef(String experimenterGroup, int imageIndex);

	void setImageExperimenterRef(String experimenter, int imageIndex);

	void setImageID(String id, int imageIndex);

	// Ignoring ImagingEnvironment element, complex property
	void setImageInstrumentRef(String instrument, int imageIndex);

	void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex);

	void setImageName(String name, int imageIndex);

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	void setImageROIRef(String roi, int imageIndex, int ROIRefIndex);

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

	void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex);

	void setImagingEnvironmentCO2Percent(PercentFraction co2Percent, int imageIndex);

	void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex);

	void setImagingEnvironmentTemperature(Double temperature, int imageIndex);

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	// Ignoring Detector element, complex property
	// Ignoring Dichroic element, complex property
	// Ignoring Filter element, complex property
	// Ignoring FilterSet element, complex property
	void setInstrumentID(String id, int instrumentIndex);

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
	void setLabelFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setLabelFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setLabelFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setLabelFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setLabelFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setLabelID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setLabelLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setLabelLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setLabelStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setLabelStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setLabelStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	void setLaserID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setLaserModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setLaserPower(Double power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
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
	void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex);

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex);

	// Manufacturer accessor from parent LightSource
	void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex);

	// Model accessor from parent LightSource
	void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex);

	// Power accessor from parent LightSource
	void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightSourceIndex);

	// SerialNumber accessor from parent LightSource
	void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex);

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

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

	// Ignoring LightSourceRef back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	void setChannelLightSourceSettingsWavelength(PositiveInteger wavelength, int imageIndex, int channelIndex);

	void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex);

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	void setLineFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setLineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setLineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setLineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setLineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setLineID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setLineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setLineLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setLineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setLineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setListAnnotationDescription(String description, int listAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setListAnnotationID(String id, int listAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setListAnnotationNamespace(String namespace, int listAnnotationIndex);

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

	void setLongAnnotationAnnotationRef(String annotation, int longAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setLongAnnotationDescription(String description, int longAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setLongAnnotationID(String id, int longAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setLongAnnotationNamespace(String namespace, int longAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setLongAnnotationValue(Long value, int longAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	void setMaskFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setMaskFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setMaskFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setMaskFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setMaskFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setMaskID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setMaskLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setMaskLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setMaskStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setMaskStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring BinData element, complex property
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

	// Ignoring Experiment_BackReference back reference
	void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex);

	void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex);

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	void setMicrobeamManipulationROIRef(String roi, int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex);

	void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex);

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

	void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex);

	void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex);

	void setObjectiveID(String id, int instrumentIndex, int objectiveIndex);

	void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex);

	// Ignoring Instrument_BackReference back reference
	void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex);

	void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex);

	void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex);

	void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex);

	void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex);

	void setObjectiveNominalMagnification(PositiveInteger nominalMagnification, int instrumentIndex, int objectiveIndex);

	void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex);

	void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex);

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex);

	void setObjectiveSettingsID(String id, int imageIndex);

	void setObjectiveSettingsMedium(Medium medium, int imageIndex);

	// Ignoring ObjectiveRef back reference
	void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex);

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setPixelsAnnotationRef(String annotation, int imageIndex, int annotationRefIndex);

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex);

	void setPixelsID(String id, int imageIndex);

	// Ignoring MetadataOnly element, complex property
	void setPixelsPhysicalSizeX(PositiveFloat physicalSizeX, int imageIndex);

	void setPixelsPhysicalSizeY(PositiveFloat physicalSizeY, int imageIndex);

	void setPixelsPhysicalSizeZ(PositiveFloat physicalSizeZ, int imageIndex);

	// Ignoring Plane element, complex property
	void setPixelsSizeC(PositiveInteger sizeC, int imageIndex);

	void setPixelsSizeT(PositiveInteger sizeT, int imageIndex);

	void setPixelsSizeX(PositiveInteger sizeX, int imageIndex);

	void setPixelsSizeY(PositiveInteger sizeY, int imageIndex);

	void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex);

	// Ignoring TiffData element, complex property
	void setPixelsTimeIncrement(Double timeIncrement, int imageIndex);

	void setPixelsType(PixelType type, int imageIndex);

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setPlaneAnnotationRef(String annotation, int imageIndex, int planeIndex, int annotationRefIndex);

	void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex);

	void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex);

	void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex);

	// Ignoring Pixels_BackReference back reference
	void setPlanePositionX(Double positionX, int imageIndex, int planeIndex);

	void setPlanePositionY(Double positionY, int imageIndex, int planeIndex);

	void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex);

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

	// Ignoring PlateAcquisition element, complex property
	void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex);

	void setPlateRows(PositiveInteger rows, int plateIndex);

	// Ignoring Screen_BackReference back reference
	void setPlateStatus(String status, int plateIndex);

	// Ignoring Well element, complex property
	void setPlateWellOriginX(Double wellOriginX, int plateIndex);

	void setPlateWellOriginY(Double wellOriginY, int plateIndex);

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

	// Ignoring Plate_BackReference back reference
	void setPlateAcquisitionStartTime(Timestamp startTime, int plateIndex, int plateAcquisitionIndex);

	void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex);

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
	void setPointFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setPointFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setPointFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setPointFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setPointFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setPointID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setPointLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setPointLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setPointStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setPointStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	void setPolygonFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setPolygonFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setPolygonFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setPolygonFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setPolygonFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setPolygonID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setPolygonLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setPolygonLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setPolygonStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setPolygonStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setPolygonStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	void setPolylineFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setPolylineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setPolylineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setPolylineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setPolylineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setPolylineID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setPolylineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setPolylineLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setPolylineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setPolylineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	void setROIAnnotationRef(String annotation, int ROIIndex, int annotationRefIndex);

	void setROIDescription(String description, int ROIIndex);

	void setROIID(String id, int ROIIndex);

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	void setROIName(String name, int ROIIndex);

	void setROINamespace(String namespace, int ROIIndex);

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

	void setReagentAnnotationRef(String annotation, int screenIndex, int reagentIndex, int annotationRefIndex);

	void setReagentDescription(String description, int screenIndex, int reagentIndex);

	void setReagentID(String id, int screenIndex, int reagentIndex);

	void setReagentName(String name, int screenIndex, int reagentIndex);

	void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex);

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
	void setRectangleFillColor(Color fillColor, int ROIIndex, int shapeIndex);

	// FillRule accessor from parent Shape
	void setRectangleFillRule(FillRule fillRule, int ROIIndex, int shapeIndex);

	// FontFamily accessor from parent Shape
	void setRectangleFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex);

	// FontSize accessor from parent Shape
	void setRectangleFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex);

	// FontStyle accessor from parent Shape
	void setRectangleFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex);

	// ID accessor from parent Shape
	void setRectangleID(String id, int ROIIndex, int shapeIndex);

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	void setRectangleLineCap(LineCap lineCap, int ROIIndex, int shapeIndex);

	// Locked accessor from parent Shape
	void setRectangleLocked(Boolean locked, int ROIIndex, int shapeIndex);

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	void setRectangleStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex);

	// StrokeDashArray accessor from parent Shape
	void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex);

	// StrokeWidth accessor from parent Shape
	void setRectangleStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex);

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

	// Ignoring Reagent element, complex property
	void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex);

	void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex);

	void setScreenType(String type, int screenIndex);

	//
	// StageLabel property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	void setStageLabelName(String name, int imageIndex);

	void setStageLabelX(Double x, int imageIndex);

	void setStageLabelY(Double y, int imageIndex);

	void setStageLabelZ(Double z, int imageIndex);

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

	void setTagAnnotationAnnotationRef(String annotation, int tagAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setTagAnnotationDescription(String description, int tagAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setTagAnnotationID(String id, int tagAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setTagAnnotationNamespace(String namespace, int tagAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setTagAnnotationValue(String value, int tagAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TermAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setTermAnnotationAnnotationRef(String annotation, int termAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setTermAnnotationDescription(String description, int termAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setTermAnnotationID(String id, int termAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setTermAnnotationNamespace(String namespace, int termAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setTermAnnotationValue(String value, int termAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	void setTiffDataFirstC(NonNegativeInteger firstC, int imageIndex, int tiffDataIndex);

	void setTiffDataFirstT(NonNegativeInteger firstT, int imageIndex, int tiffDataIndex);

	void setTiffDataFirstZ(NonNegativeInteger firstZ, int imageIndex, int tiffDataIndex);

	void setTiffDataIFD(NonNegativeInteger ifd, int imageIndex, int tiffDataIndex);

	// Ignoring Pixels_BackReference back reference
	void setTiffDataPlaneCount(NonNegativeInteger planeCount, int imageIndex, int tiffDataIndex);

	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	void setTimestampAnnotationAnnotationRef(String annotation, int timestampAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setTimestampAnnotationDescription(String description, int timestampAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setTimestampAnnotationID(String id, int timestampAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setTimestampAnnotationValue(Timestamp value, int timestampAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	void setTransmittanceRangeCutIn(PositiveInteger cutIn, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutInTolerance(NonNegativeInteger cutInTolerance, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutOut(PositiveInteger cutOut, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeCutOutTolerance(NonNegativeInteger cutOutTolerance, int instrumentIndex, int filterIndex);

	void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex);

	// Element's text data
	// {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
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

	// Ignoring Shape element, complex property
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

	// Ignoring Plate_BackReference back reference
	void setWellReagentRef(String reagent, int plateIndex, int wellIndex);

	void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex);

	void setWellType(String type, int plateIndex, int wellIndex);

	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	void setWellSampleAnnotationRef(String annotation, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex);

	void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex);

	// Ignoring PlateAcquisition_BackReference back reference
	void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex);

	void setWellSampleTimepoint(Timestamp timepoint, int plateIndex, int wellIndex, int wellSampleIndex);

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

	void setXMLAnnotationAnnotationRef(String annotation, int XMLAnnotationIndex, int annotationRefIndex);

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	void setXMLAnnotationDescription(String description, int XMLAnnotationIndex);

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	void setXMLAnnotationID(String id, int XMLAnnotationIndex);

	// Ignoring Image_BackReference back reference
	void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex);

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	void setXMLAnnotationValue(String value, int XMLAnnotationIndex);

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
