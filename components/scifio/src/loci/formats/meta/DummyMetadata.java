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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:39.369548
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import ome.xml.model.*;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

/**
 * A dummy implementation for {@link MetadataStore} and
 * {@link MetadataRetrieve} that is used when no other
 * metadata implementations are available.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/meta/DummyMetadata.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/meta/DummyMetadata.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class DummyMetadata implements IMetadata
{
	// -- MetadataStore API methods --

	public void createRoot()
	{
	}

	public Object getRoot()
	{
		return null;
	}

	public void setRoot(Object root)
	{
	}

	// -- Entity counting (manual definitions) --

	public int getPixelsBinDataCount(int imageIndex)
	{
		return -1;
	}

  public int getBooleanAnnotationAnnotationCount(int booleanAnnotationIndex) {
    return -1;
  }

  public int getCommentAnnotationAnnotationCount(int commentAnnotationIndex) {
    return -1;
  }

  public int getDoubleAnnotationAnnotationCount(int doubleAnnotationIndex) {
    return -1;
  }

  public int getFileAnnotationAnnotationCount(int fileAnnotationIndex) {
    return -1;
  }

  public int getListAnnotationAnnotationCount(int listAnnotationIndex) {
    return -1;
  }

  public int getLongAnnotationAnnotationCount(int longAnnotationIndex) {
    return -1;
  }

  public int getTagAnnotationAnnotationCount(int tagAnnotationIndex) {
    return -1;
  }

  public int getTermAnnotationAnnotationCount(int termAnnotationIndex) {
    return -1;
  }

  public int getTimestampAnnotationAnnotationCount(int timestampAnnotationIndex)
  {
    return -1;
  }

  public int getXMLAnnotationAnnotationCount(int xmlAnnotationIndex) {
    return -1;
  }

	public String getLightSourceType(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

  public String getShapeType(int roiIndex, int shapeIndex)
  {
    return null;
  }

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	public int getROIAnnotationRefCount(int ROIIndex)
	{
		return -1;
	}

	public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		return -1;
	}

	public int getPlateAnnotationRefCount(int plateIndex)
	{
		return -1;
	}

	public int getExperimenterGroupAnnotationRefCount(int experimenterGroupIndex)
	{
		return -1;
	}

	public int getImageAnnotationRefCount(int imageIndex)
	{
		return -1;
	}

	public int getScreenAnnotationRefCount(int screenIndex)
	{
		return -1;
	}

	public int getWellAnnotationRefCount(int plateIndex, int wellIndex)
	{
		return -1;
	}

	public int getDatasetAnnotationRefCount(int datasetIndex)
	{
		return -1;
	}

	public int getProjectAnnotationRefCount(int projectIndex)
	{
		return -1;
	}

	public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex)
	{
		return -1;
	}

	public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex)
	{
		return -1;
	}

	public int getExperimenterAnnotationRefCount(int experimenterIndex)
	{
		return -1;
	}

	public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return -1;
	}

	public int getPixelsAnnotationRefCount(int imageIndex)
	{
		return -1;
	}

	public int getChannelAnnotationRefCount(int imageIndex, int channelIndex)
	{
		return -1;
	}

	// Arc entity counting
	// BinaryFile entity counting
	// BinaryOnly entity counting
	// BooleanAnnotation entity counting
	public int getBooleanAnnotationCount()
	{
		return -1;
	}

	// Channel entity counting
	public int getChannelCount(int imageIndex)
	{
		return -1;
	}

	// CommentAnnotation entity counting
	public int getCommentAnnotationCount()
	{
		return -1;
	}

	// Dataset entity counting
	public int getDatasetCount()
	{
		return -1;
	}

	// DatasetRef entity counting
	public int getDatasetRefCount(int projectIndex)
	{
		return -1;
	}

	// Detector entity counting
	public int getDetectorCount(int instrumentIndex)
	{
		return -1;
	}

	// DetectorSettings entity counting
	// Dichroic entity counting
	public int getDichroicCount(int instrumentIndex)
	{
		return -1;
	}

	// DichroicRef entity counting
	// DoubleAnnotation entity counting
	public int getDoubleAnnotationCount()
	{
		return -1;
	}

	// Ellipse entity counting
	// EmissionFilterRef entity counting
	public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex)
	{
		return -1;
	}

	public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		return -1;
	}

	// ExcitationFilterRef entity counting
	public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex)
	{
		return -1;
	}

	public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		return -1;
	}

	// Experiment entity counting
	public int getExperimentCount()
	{
		return -1;
	}

	// ExperimentRef entity counting
	// Experimenter entity counting
	public int getExperimenterCount()
	{
		return -1;
	}

	// ExperimenterGroup entity counting
	public int getExperimenterGroupCount()
	{
		return -1;
	}

	// ExperimenterGroupRef entity counting
	// ExperimenterRef entity counting
	public int getExperimenterGroupExperimenterRefCount(int experimenterGroupIndex)
	{
		return -1;
	}

	// Filament entity counting
	// FileAnnotation entity counting
	public int getFileAnnotationCount()
	{
		return -1;
	}

	// Filter entity counting
	public int getFilterCount(int instrumentIndex)
	{
		return -1;
	}

	// FilterSet entity counting
	public int getFilterSetCount(int instrumentIndex)
	{
		return -1;
	}

	// FilterSetRef entity counting
	// Image entity counting
	public int getImageCount()
	{
		return -1;
	}

	// ImageRef entity counting
	public int getDatasetImageRefCount(int datasetIndex)
	{
		return -1;
	}

	// ImagingEnvironment entity counting
	// Instrument entity counting
	public int getInstrumentCount()
	{
		return -1;
	}

	// InstrumentRef entity counting
	// Label entity counting
	// Laser entity counting
	// Leader entity counting
	public int getLeaderCount(int experimenterGroupIndex)
	{
		return -1;
	}

	// LightEmittingDiode entity counting
	// LightPath entity counting
	// LightSource entity counting
	public int getLightSourceCount(int instrumentIndex)
	{
		return -1;
	}

	// LightSourceSettings entity counting
	public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex)
	{
		return -1;
	}

	// Line entity counting
	// ListAnnotation entity counting
	public int getListAnnotationCount()
	{
		return -1;
	}

	// LongAnnotation entity counting
	public int getLongAnnotationCount()
	{
		return -1;
	}

	// Mask entity counting
	// MetadataOnly entity counting
	// MicrobeamManipulation entity counting
	public int getMicrobeamManipulationCount(int experimentIndex)
	{
		return -1;
	}

	// MicrobeamManipulationRef entity counting
	public int getMicrobeamManipulationRefCount(int imageIndex)
	{
		return -1;
	}

	// Microscope entity counting
	// Objective entity counting
	public int getObjectiveCount(int instrumentIndex)
	{
		return -1;
	}

	// ObjectiveSettings entity counting
	// Pixels entity counting
	// Plane entity counting
	public int getPlaneCount(int imageIndex)
	{
		return -1;
	}

	// Plate entity counting
	public int getPlateCount()
	{
		return -1;
	}

	// PlateAcquisition entity counting
	public int getPlateAcquisitionCount(int plateIndex)
	{
		return -1;
	}

	// PlateRef entity counting
	public int getPlateRefCount(int screenIndex)
	{
		return -1;
	}

	// Point entity counting
	// Polygon entity counting
	// Polyline entity counting
	// Project entity counting
	public int getProjectCount()
	{
		return -1;
	}

	// Pump entity counting
	// ROI entity counting
	public int getROICount()
	{
		return -1;
	}

	// ROIRef entity counting
	public int getImageROIRefCount(int imageIndex)
	{
		return -1;
	}

	public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex)
	{
		return -1;
	}

	// Reagent entity counting
	public int getReagentCount(int screenIndex)
	{
		return -1;
	}

	// ReagentRef entity counting
	// Rectangle entity counting
	// Screen entity counting
	public int getScreenCount()
	{
		return -1;
	}

	// Shape entity counting
	public int getShapeCount(int ROIIndex)
	{
		return -1;
	}

	// StageLabel entity counting
	// StructuredAnnotations entity counting
	// TagAnnotation entity counting
	public int getTagAnnotationCount()
	{
		return -1;
	}

	// TermAnnotation entity counting
	public int getTermAnnotationCount()
	{
		return -1;
	}

	// TiffData entity counting
	public int getTiffDataCount(int imageIndex)
	{
		return -1;
	}

	// TimestampAnnotation entity counting
	public int getTimestampAnnotationCount()
	{
		return -1;
	}

	// TransmittanceRange entity counting
	// Element's text data
	// {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	public void setUUIDValue(String value, int imageIndex, int tiffDataIndex)
	{
	}

	public String getUUIDValue(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	// UUID entity counting
	// Union entity counting
	// Well entity counting
	public int getWellCount(int plateIndex)
	{
		return -1;
	}

	// WellSample entity counting
	public int getWellSampleCount(int plateIndex, int wellIndex)
	{
		return -1;
	}

	// WellSampleRef entity counting
	public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		return -1;
	}

	// XMLAnnotation entity counting
	public int getXMLAnnotationCount()
	{
		return -1;
	}


	// -- Entity retrieval (manual definitions) --

	public Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex)
	{
		return null;
	}

	// -- Entity retrieval (code generated definitions) --

	/** Gets the UUID associated with this collection of metadata. */
	public String getUUID()
	{
		return null;
	}

	//
	// AnnotationRef property storage
	//
	// Indexes: {u'ROI': [u'int ROIIndex', u'int annotationRefIndex'], u'Reagent': [u'int screenIndex', u'int reagentIndex', u'int annotationRefIndex'], u'Plate': [u'int plateIndex', u'int annotationRefIndex'], u'ExperimenterGroup': [u'int experimenterGroupIndex', u'int annotationRefIndex'], u'Image': [u'int imageIndex', u'int annotationRefIndex'], u'Well': [u'int plateIndex', u'int wellIndex', u'int annotationRefIndex'], u'Pixels': [u'int imageIndex', u'int annotationRefIndex'], u'Dataset': [u'int datasetIndex', u'int annotationRefIndex'], u'Project': [u'int projectIndex', u'int annotationRefIndex'], u'PlateAcquisition': [u'int plateIndex', u'int plateAcquisitionIndex', u'int annotationRefIndex'], u'Plane': [u'int imageIndex', u'int planeIndex', u'int annotationRefIndex'], u'Experimenter': [u'int experimenterIndex', u'int annotationRefIndex'], u'Annotation': [u'int annotationRefIndex'], u'WellSample': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex', u'int annotationRefIndex'], u'Screen': [u'int screenIndex', u'int annotationRefIndex'], u'Channel': [u'int imageIndex', u'int channelIndex', u'int annotationRefIndex']}
	// {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference AnnotationRef

	//
	// Arc property storage
	//
	// Indexes: {u'LightSource': [u'int instrumentIndex', u'int lightSourceIndex']}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	public String getArcID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getArcLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getArcManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Model accessor from parent LightSource
	public String getArcModel(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Power accessor from parent LightSource
	public Double getArcPower(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getArcSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public ArcType getArcType(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	//
	// BinaryFile property storage
	//
	// Indexes: {u'FileAnnotation': [u'int fileAnnotationIndex']}
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	public String getBinaryFileFileName(int fileAnnotationIndex)
	{
		return null;
	}

	public String getBinaryFileMIMEType(int fileAnnotationIndex)
	{
		return null;
	}

	public NonNegativeLong getBinaryFileSize(int fileAnnotationIndex)
	{
		return null;
	}

	//
	// BinaryOnly property storage
	//
	// Indexes: {u'OME': []}
	// {u'OME': None}
	// Is multi path? False

	public String getBinaryOnlyMetadataFile(int metadataFileIndex)
	{
		return null;
	}

	public String getBinaryOnlyUUID(int UUIDIndex)
	{
		return null;
	}

	//
	// BooleanAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int booleanAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getBooleanAnnotationAnnotationRef(int booleanAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getBooleanAnnotationDescription(int booleanAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getBooleanAnnotationID(int booleanAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getBooleanAnnotationNamespace(int booleanAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public Boolean getBooleanAnnotationValue(int booleanAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Channel property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int channelIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex)
	{
		return null;
	}

	public Color getChannelColor(int imageIndex, int channelIndex)
	{
		return null;
	}

	public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex)
	{
		return null;
	}

	// Ignoring DetectorSettings element, complex property
	public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex)
	{
		return null;
	}

	public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getChannelFilterSetRef(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getChannelFluor(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getChannelID(int imageIndex, int channelIndex)
	{
		return null;
	}

	public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex)
	{
		return null;
	}

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	public Double getChannelNDFilter(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getChannelName(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getChannelPinholeSize(int imageIndex, int channelIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	public Integer getChannelPockelCellSetting(int imageIndex, int channelIndex)
	{
		return null;
	}

	public PositiveInteger getChannelSamplesPerPixel(int imageIndex, int channelIndex)
	{
		return null;
	}

	//
	// CommentAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int commentAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getCommentAnnotationAnnotationRef(int commentAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getCommentAnnotationDescription(int commentAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getCommentAnnotationID(int commentAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getCommentAnnotationNamespace(int commentAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public String getCommentAnnotationValue(int commentAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Dataset property storage
	//
	// Indexes: {u'OME': [u'int datasetIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getDatasetDescription(int datasetIndex)
	{
		return null;
	}

	public String getDatasetExperimenterGroupRef(int datasetIndex)
	{
		return null;
	}

	public String getDatasetExperimenterRef(int datasetIndex)
	{
		return null;
	}

	public String getDatasetID(int datasetIndex)
	{
		return null;
	}

	public String getDatasetImageRef(int datasetIndex, int imageRefIndex)
	{
		return null;
	}

	public String getDatasetName(int datasetIndex)
	{
		return null;
	}

	// Ignoring Project_BackReference back reference
	//
	// DatasetRef property storage
	//
	// Indexes: {u'Project': [u'int projectIndex', u'int datasetRefIndex']}
	// {u'Project': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference DatasetRef

	//
	// Detector property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int detectorIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public Double getDetectorGain(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public String getDetectorID(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	// Ignoring Instrument_BackReference back reference
	public String getDetectorLotNumber(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public String getDetectorManufacturer(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public String getDetectorModel(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public Double getDetectorOffset(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public DetectorType getDetectorType(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public Double getDetectorVoltage(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public Double getDetectorZoom(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	//
	// DetectorSettings property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex)
	{
		return null;
	}

	// Ignoring DetectorRef back reference
	public Double getDetectorSettingsGain(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getDetectorSettingsID(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getDetectorSettingsOffset(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getDetectorSettingsVoltage(int imageIndex, int channelIndex)
	{
		return null;
	}

	//
	// Dichroic property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int dichroicIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public String getDichroicID(int instrumentIndex, int dichroicIndex)
	{
		return null;
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex)
	{
		return null;
	}

	public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex)
	{
		return null;
	}

	public String getDichroicModel(int instrumentIndex, int dichroicIndex)
	{
		return null;
	}

	public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex)
	{
		return null;
	}

	//
	// DichroicRef property storage
	//
	// Indexes: {u'LightPath': [u'int imageIndex', u'int channelIndex'], u'FilterSet': [u'int instrumentIndex', u'int filterSetIndex']}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference DichroicRef

	//
	// DoubleAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int doubleAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getDoubleAnnotationAnnotationRef(int doubleAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getDoubleAnnotationDescription(int doubleAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getDoubleAnnotationID(int doubleAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getDoubleAnnotationNamespace(int doubleAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public Double getDoubleAnnotationValue(int doubleAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getEllipseFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getEllipseFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getEllipseFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getEllipseFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getEllipseFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getEllipseID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getEllipseLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getEllipseLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getEllipseStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getEllipseText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getEllipseTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getEllipseTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getEllipseTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getEllipseTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getEllipseVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getEllipseRadiusX(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getEllipseRadiusY(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getEllipseX(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getEllipseY(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// EmissionFilterRef property storage
	//
	// Indexes: {u'LightPath': [u'int imageIndex', u'int channelIndex', u'int emissionFilterRefIndex'], u'FilterSet': [u'int instrumentIndex', u'int filterSetIndex', u'int emissionFilterRefIndex']}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// ExcitationFilterRef property storage
	//
	// Indexes: {u'LightPath': [u'int imageIndex', u'int channelIndex', u'int excitationFilterRefIndex'], u'FilterSet': [u'int instrumentIndex', u'int filterSetIndex', u'int excitationFilterRefIndex']}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// Experiment property storage
	//
	// Indexes: {u'OME': [u'int experimentIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimentDescription(int experimentIndex)
	{
		return null;
	}

	public String getExperimentExperimenterRef(int experimentIndex)
	{
		return null;
	}

	public String getExperimentID(int experimentIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	public ExperimentType getExperimentType(int experimentIndex)
	{
		return null;
	}

	//
	// ExperimentRef property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ExperimentRef

	//
	// Experimenter property storage
	//
	// Indexes: {u'OME': [u'int experimenterIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Dataset_BackReference back reference
	public String getExperimenterEmail(int experimenterIndex)
	{
		return null;
	}

	// Ignoring Experiment_BackReference back reference
	// Ignoring ExperimenterGroup_BackReference back reference
	public String getExperimenterFirstName(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterID(int experimenterIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getExperimenterInstitution(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterLastName(int experimenterIndex)
	{
		return null;
	}

	// Ignoring MicrobeamManipulation_BackReference back reference
	public String getExperimenterMiddleName(int experimenterIndex)
	{
		return null;
	}

	// Ignoring Project_BackReference back reference
	public String getExperimenterUserName(int experimenterIndex)
	{
		return null;
	}

	//
	// ExperimenterGroup property storage
	//
	// Indexes: {u'OME': [u'int experimenterGroupIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimenterGroupAnnotationRef(int experimenterGroupIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Dataset_BackReference back reference
	public String getExperimenterGroupDescription(int experimenterGroupIndex)
	{
		return null;
	}

	public String getExperimenterGroupExperimenterRef(int experimenterGroupIndex, int experimenterRefIndex)
	{
		return null;
	}

	public String getExperimenterGroupID(int experimenterGroupIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getExperimenterGroupLeader(int experimenterGroupIndex, int leaderIndex)
	{
		return null;
	}

	public String getExperimenterGroupName(int experimenterGroupIndex)
	{
		return null;
	}

	// Ignoring Project_BackReference back reference
	//
	// ExperimenterGroupRef property storage
	//
	// Indexes: {u'Project': [u'int projectIndex'], u'Image': [u'int imageIndex'], u'Dataset': [u'int datasetIndex']}
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ExperimenterGroupRef

	//
	// ExperimenterRef property storage
	//
	// Indexes: {u'ExperimenterGroup': [u'int experimenterGroupIndex', u'int experimenterRefIndex'], u'Image': [u'int imageIndex'], u'Dataset': [u'int datasetIndex'], u'Project': [u'int projectIndex'], u'Experiment': [u'int experimentIndex'], u'MicrobeamManipulation': [u'int experimentIndex', u'int microbeamManipulationIndex']}
	// {u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ExperimenterRef

	//
	// Filament property storage
	//
	// Indexes: {u'LightSource': [u'int instrumentIndex', u'int lightSourceIndex']}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	public String getFilamentID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Model accessor from parent LightSource
	public String getFilamentModel(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Power accessor from parent LightSource
	public Double getFilamentPower(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	//
	// FileAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int fileAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getFileAnnotationAnnotationRef(int fileAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getFileAnnotationDescription(int fileAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getFileAnnotationID(int fileAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getFileAnnotationNamespace(int fileAnnotationIndex)
	{
		return null;
	}

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
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int filterIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	// Ignoring FilterSet_BackReference back reference
	public String getFilterFilterWheel(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public String getFilterID(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public String getFilterLotNumber(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public String getFilterManufacturer(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public String getFilterModel(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public String getFilterSerialNumber(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	// Ignoring TransmittanceRange element, complex property
	public FilterType getFilterType(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	//
	// FilterSet property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int filterSetIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	public String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex)
	{
		return null;
	}

	public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
		return null;
	}

	public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
		return null;
	}

	public String getFilterSetID(int instrumentIndex, int filterSetIndex)
	{
		return null;
	}

	// Ignoring Instrument_BackReference back reference
	public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex)
	{
		return null;
	}

	public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex)
	{
		return null;
	}

	public String getFilterSetModel(int instrumentIndex, int filterSetIndex)
	{
		return null;
	}

	public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex)
	{
		return null;
	}

	//
	// FilterSetRef property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference FilterSetRef

	//
	// Image property storage
	//
	// Indexes: {u'OME': [u'int imageIndex']}
	// {u'OME': None}
	// Is multi path? False

	public Timestamp getImageAcquisitionDate(int imageIndex)
	{
		return null;
	}

	public String getImageAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Dataset_BackReference back reference
	public String getImageDescription(int imageIndex)
	{
		return null;
	}

	public String getImageExperimentRef(int imageIndex)
	{
		return null;
	}

	public String getImageExperimenterGroupRef(int imageIndex)
	{
		return null;
	}

	public String getImageExperimenterRef(int imageIndex)
	{
		return null;
	}

	public String getImageID(int imageIndex)
	{
		return null;
	}

	// Ignoring ImagingEnvironment element, complex property
	public String getImageInstrumentRef(int imageIndex)
	{
		return null;
	}

	public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex)
	{
		return null;
	}

	public String getImageName(int imageIndex)
	{
		return null;
	}

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	public String getImageROIRef(int imageIndex, int ROIRefIndex)
	{
		return null;
	}

	// Ignoring StageLabel element, complex property
	// Ignoring WellSample_BackReference back reference
	//
	// ImageRef property storage
	//
	// Indexes: {u'WellSample': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'], u'Dataset': [u'int datasetIndex', u'int imageRefIndex']}
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ImageRef

	//
	// ImagingEnvironment property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public Double getImagingEnvironmentAirPressure(int imageIndex)
	{
		return null;
	}

	public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex)
	{
		return null;
	}

	public PercentFraction getImagingEnvironmentHumidity(int imageIndex)
	{
		return null;
	}

	public Double getImagingEnvironmentTemperature(int imageIndex)
	{
		return null;
	}

	//
	// Instrument property storage
	//
	// Indexes: {u'OME': [u'int instrumentIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Ignoring Detector element, complex property
	// Ignoring Dichroic element, complex property
	// Ignoring Filter element, complex property
	// Ignoring FilterSet element, complex property
	public String getInstrumentID(int instrumentIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSource element, complex property
	// Ignoring Microscope element, complex property
	// Ignoring Objective element, complex property
	//
	// InstrumentRef property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference InstrumentRef

	//
	// Label property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getLabelFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getLabelFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getLabelFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getLabelFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getLabelFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getLabelID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getLabelLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getLabelLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getLabelStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getLabelStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getLabelStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getLabelText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getLabelTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getLabelTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getLabelTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getLabelTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getLabelVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getLabelX(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getLabelY(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// Laser property storage
	//
	// Indexes: {u'LightSource': [u'int instrumentIndex', u'int lightSourceIndex']}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	public String getLaserID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLaserLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getLaserManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Model accessor from parent LightSource
	public String getLaserModel(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Power accessor from parent LightSource
	public Double getLaserPower(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getLaserSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public LaserMedium getLaserLaserMedium(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public Pulse getLaserPulse(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public String getLaserPump(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public Double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public LaserType getLaserType(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	public PositiveInteger getLaserWavelength(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	//
	// Leader property storage
	//
	// Indexes: {u'ExperimenterGroup': [u'int experimenterGroupIndex', u'int leaderIndex']}
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
	// Indexes: {u'LightSource': [u'int instrumentIndex', u'int lightSourceIndex']}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	public String getLightEmittingDiodeID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Model accessor from parent LightSource
	public String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Power accessor from parent LightSource
	public Double getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	//
	// LightPath property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getLightPathDichroicRef(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
		return null;
	}

	public String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex)
	{
		return null;
	}

	//
	// LightSourceSettings property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex'], u'MicrobeamManipulation': [u'int experimentIndex', u'int microbeamManipulationIndex', u'int lightSourceSettingsIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	public PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex)
	{
		return null;
	}

	public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		return null;
	}

	public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		return null;
	}

	// Ignoring LightSourceRef back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex)
	{
		return null;
	}

	public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		return null;
	}

	//
	// Line property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getLineFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getLineFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getLineFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getLineFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getLineFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getLineID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getLineLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getLineLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getLineStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getLineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getLineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getLineText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getLineTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getLineTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getLineTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getLineTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getLineVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Marker getLineMarkerEnd(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Marker getLineMarkerStart(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getLineX1(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getLineX2(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getLineY1(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getLineY2(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// ListAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int listAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getListAnnotationDescription(int listAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getListAnnotationID(int listAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getListAnnotationNamespace(int listAnnotationIndex)
	{
		return null;
	}

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
	// Indexes: {u'StructuredAnnotations': [u'int longAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getLongAnnotationAnnotationRef(int longAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getLongAnnotationDescription(int longAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getLongAnnotationID(int longAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getLongAnnotationNamespace(int longAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public Long getLongAnnotationValue(int longAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getMaskFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getMaskFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getMaskFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getMaskFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getMaskFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getMaskID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getMaskLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getMaskLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getMaskStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getMaskStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getMaskStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getMaskText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getMaskTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getMaskTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getMaskTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getMaskTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getMaskVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring BinData element, complex property
	public Double getMaskHeight(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getMaskWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getMaskX(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getMaskY(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// MetadataOnly property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	//
	// MicrobeamManipulation property storage
	//
	// Indexes: {u'Experiment': [u'int experimentIndex', u'int microbeamManipulationIndex']}
	// {u'Experiment': {u'OME': None}}
	// Is multi path? False

	public String getMicrobeamManipulationDescription(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

	// Ignoring Experiment_BackReference back reference
	public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

	public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
		return null;
	}

	public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

	//
	// MicrobeamManipulationRef property storage
	//
	// Indexes: {u'Image': [u'int imageIndex', u'int microbeamManipulationRefIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference MicrobeamManipulationRef

	//
	// Microscope property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public String getMicroscopeLotNumber(int instrumentIndex)
	{
		return null;
	}

	public String getMicroscopeManufacturer(int instrumentIndex)
	{
		return null;
	}

	public String getMicroscopeModel(int instrumentIndex)
	{
		return null;
	}

	public String getMicroscopeSerialNumber(int instrumentIndex)
	{
		return null;
	}

	public MicroscopeType getMicroscopeType(int instrumentIndex)
	{
		return null;
	}

	//
	// Objective property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int objectiveIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public String getObjectiveID(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	// Ignoring Instrument_BackReference back reference
	public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public String getObjectiveModel(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public PositiveInteger getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	//
	// ObjectiveSettings property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public Double getObjectiveSettingsCorrectionCollar(int imageIndex)
	{
		return null;
	}

	public String getObjectiveSettingsID(int imageIndex)
	{
		return null;
	}

	public Medium getObjectiveSettingsMedium(int imageIndex)
	{
		return null;
	}

	// Ignoring ObjectiveRef back reference
	public Double getObjectiveSettingsRefractiveIndex(int imageIndex)
	{
		return null;
	}

	//
	// Pixels property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	public DimensionOrder getPixelsDimensionOrder(int imageIndex)
	{
		return null;
	}

	public String getPixelsID(int imageIndex)
	{
		return null;
	}

	// Ignoring MetadataOnly element, complex property
	public PositiveFloat getPixelsPhysicalSizeX(int imageIndex)
	{
		return null;
	}

	public PositiveFloat getPixelsPhysicalSizeY(int imageIndex)
	{
		return null;
	}

	public PositiveFloat getPixelsPhysicalSizeZ(int imageIndex)
	{
		return null;
	}

	// Ignoring Plane element, complex property
	public PositiveInteger getPixelsSizeC(int imageIndex)
	{
		return null;
	}

	public PositiveInteger getPixelsSizeT(int imageIndex)
	{
		return null;
	}

	public PositiveInteger getPixelsSizeX(int imageIndex)
	{
		return null;
	}

	public PositiveInteger getPixelsSizeY(int imageIndex)
	{
		return null;
	}

	public PositiveInteger getPixelsSizeZ(int imageIndex)
	{
		return null;
	}

	// Ignoring TiffData element, complex property
	public Double getPixelsTimeIncrement(int imageIndex)
	{
		return null;
	}

	public PixelType getPixelsType(int imageIndex)
	{
		return null;
	}

	//
	// Plane property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int planeIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex)
	{
		return null;
	}

	public Double getPlaneDeltaT(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Double getPlaneExposureTime(int imageIndex, int planeIndex)
	{
		return null;
	}

	public String getPlaneHashSHA1(int imageIndex, int planeIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	public Double getPlanePositionX(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Double getPlanePositionY(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Double getPlanePositionZ(int imageIndex, int planeIndex)
	{
		return null;
	}

	public NonNegativeInteger getPlaneTheC(int imageIndex, int planeIndex)
	{
		return null;
	}

	public NonNegativeInteger getPlaneTheT(int imageIndex, int planeIndex)
	{
		return null;
	}

	public NonNegativeInteger getPlaneTheZ(int imageIndex, int planeIndex)
	{
		return null;
	}

	//
	// Plate property storage
	//
	// Indexes: {u'OME': [u'int plateIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getPlateAnnotationRef(int plateIndex, int annotationRefIndex)
	{
		return null;
	}

	public NamingConvention getPlateColumnNamingConvention(int plateIndex)
	{
		return null;
	}

	public PositiveInteger getPlateColumns(int plateIndex)
	{
		return null;
	}

	public String getPlateDescription(int plateIndex)
	{
		return null;
	}

	public String getPlateExternalIdentifier(int plateIndex)
	{
		return null;
	}

	public NonNegativeInteger getPlateFieldIndex(int plateIndex)
	{
		return null;
	}

	public String getPlateID(int plateIndex)
	{
		return null;
	}

	public String getPlateName(int plateIndex)
	{
		return null;
	}

	// Ignoring PlateAcquisition element, complex property
	public NamingConvention getPlateRowNamingConvention(int plateIndex)
	{
		return null;
	}

	public PositiveInteger getPlateRows(int plateIndex)
	{
		return null;
	}

	// Ignoring Screen_BackReference back reference
	public String getPlateStatus(int plateIndex)
	{
		return null;
	}

	// Ignoring Well element, complex property
	public Double getPlateWellOriginX(int plateIndex)
	{
		return null;
	}

	public Double getPlateWellOriginY(int plateIndex)
	{
		return null;
	}

	//
	// PlateAcquisition property storage
	//
	// Indexes: {u'Plate': [u'int plateIndex', u'int plateAcquisitionIndex']}
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex)
	{
		return null;
	}

	public Timestamp getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex)
	{
		return null;
	}

	public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex)
	{
		return null;
	}

	public PositiveInteger getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex)
	{
		return null;
	}

	public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex)
	{
		return null;
	}

	// Ignoring Plate_BackReference back reference
	public Timestamp getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex)
	{
		return null;
	}

	public String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
	{
		return null;
	}

	//
	// PlateRef property storage
	//
	// Indexes: {u'Screen': [u'int screenIndex', u'int plateRefIndex']}
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference PlateRef

	//
	// Point property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getPointFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getPointFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPointFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getPointFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPointFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getPointID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getPointLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getPointLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getPointStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPointStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getPointStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getPointText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPointTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPointTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPointTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getPointTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getPointVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getPointX(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getPointY(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// Polygon property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getPolygonFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getPolygonFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPolygonFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getPolygonFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPolygonFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getPolygonID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getPolygonLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getPolygonLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getPolygonStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPolygonStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getPolygonStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getPolygonText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPolygonTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPolygonTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPolygonTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getPolygonTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getPolygonVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public String getPolygonPoints(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// Polyline property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getPolylineFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getPolylineFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPolylineFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getPolylineFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPolylineFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getPolylineID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getPolylineLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getPolylineLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getPolylineStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getPolylineText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPolylineTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPolylineTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPolylineTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getPolylineTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getPolylineVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Marker getPolylineMarkerEnd(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Marker getPolylineMarkerStart(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public String getPolylinePoints(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// Project property storage
	//
	// Indexes: {u'OME': [u'int projectIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getProjectAnnotationRef(int projectIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getProjectDatasetRef(int projectIndex, int datasetRefIndex)
	{
		return null;
	}

	public String getProjectDescription(int projectIndex)
	{
		return null;
	}

	public String getProjectExperimenterGroupRef(int projectIndex)
	{
		return null;
	}

	public String getProjectExperimenterRef(int projectIndex)
	{
		return null;
	}

	public String getProjectID(int projectIndex)
	{
		return null;
	}

	public String getProjectName(int projectIndex)
	{
		return null;
	}

	//
	// Pump property storage
	//
	// Indexes: {u'Laser': [u'int instrumentIndex', u'int lightSourceIndex']}
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
	// Indexes: {u'OME': [u'int ROIIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getROIAnnotationRef(int ROIIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getROIDescription(int ROIIndex)
	{
		return null;
	}

	public String getROIID(int ROIIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public String getROIName(int ROIIndex)
	{
		return null;
	}

	public String getROINamespace(int ROIIndex)
	{
		return null;
	}

	// Ignoring Union element, complex property
	//
	// ROIRef property storage
	//
	// Indexes: {u'Image': [u'int imageIndex', u'int ROIRefIndex'], u'MicrobeamManipulation': [u'int experimentIndex', u'int microbeamManipulationIndex', u'int ROIRefIndex']}
	// {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ROIRef

	//
	// Reagent property storage
	//
	// Indexes: {u'Screen': [u'int screenIndex', u'int reagentIndex']}
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	public String getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getReagentDescription(int screenIndex, int reagentIndex)
	{
		return null;
	}

	public String getReagentID(int screenIndex, int reagentIndex)
	{
		return null;
	}

	public String getReagentName(int screenIndex, int reagentIndex)
	{
		return null;
	}

	public String getReagentReagentIdentifier(int screenIndex, int reagentIndex)
	{
		return null;
	}

	// Ignoring Screen_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// ReagentRef property storage
	//
	// Indexes: {u'Well': [u'int plateIndex', u'int wellIndex']}
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ReagentRef

	//
	// Rectangle property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getRectangleFillColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getRectangleFillRule(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getRectangleFontFamily(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getRectangleFontSize(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getRectangleFontStyle(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// ID accessor from parent Shape
	public String getRectangleID(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getRectangleLineCap(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getRectangleLocked(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getRectangleStrokeColor(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Text accessor from parent Shape
	public String getRectangleText(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getRectangleTheC(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getRectangleTheT(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getRectangleTheZ(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getRectangleTransform(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getRectangleVisible(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getRectangleHeight(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getRectangleWidth(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getRectangleX(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	public Double getRectangleY(int ROIIndex, int shapeIndex)
	{
		return null;
	}

	//
	// Screen property storage
	//
	// Indexes: {u'OME': [u'int screenIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getScreenAnnotationRef(int screenIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getScreenDescription(int screenIndex)
	{
		return null;
	}

	public String getScreenID(int screenIndex)
	{
		return null;
	}

	public String getScreenName(int screenIndex)
	{
		return null;
	}

	public String getScreenPlateRef(int screenIndex, int plateRefIndex)
	{
		return null;
	}

	public String getScreenProtocolDescription(int screenIndex)
	{
		return null;
	}

	public String getScreenProtocolIdentifier(int screenIndex)
	{
		return null;
	}

	// Ignoring Reagent element, complex property
	public String getScreenReagentSetDescription(int screenIndex)
	{
		return null;
	}

	public String getScreenReagentSetIdentifier(int screenIndex)
	{
		return null;
	}

	public String getScreenType(int screenIndex)
	{
		return null;
	}

	//
	// StageLabel property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public String getStageLabelName(int imageIndex)
	{
		return null;
	}

	public Double getStageLabelX(int imageIndex)
	{
		return null;
	}

	public Double getStageLabelY(int imageIndex)
	{
		return null;
	}

	public Double getStageLabelZ(int imageIndex)
	{
		return null;
	}

	//
	// StructuredAnnotations property storage
	//
	// Indexes: {u'OME': []}
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
	// Indexes: {u'StructuredAnnotations': [u'int tagAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getTagAnnotationAnnotationRef(int tagAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTagAnnotationDescription(int tagAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getTagAnnotationID(int tagAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getTagAnnotationNamespace(int tagAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public String getTagAnnotationValue(int tagAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TermAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int termAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getTermAnnotationAnnotationRef(int termAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTermAnnotationDescription(int termAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getTermAnnotationID(int termAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getTermAnnotationNamespace(int termAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public String getTermAnnotationValue(int termAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TiffData property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int tiffDataIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public NonNegativeInteger getTiffDataFirstC(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	public NonNegativeInteger getTiffDataFirstT(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	public NonNegativeInteger getTiffDataFirstZ(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	public NonNegativeInteger getTiffDataIFD(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	public NonNegativeInteger getTiffDataPlaneCount(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int timestampAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getTimestampAnnotationAnnotationRef(int timestampAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTimestampAnnotationDescription(int timestampAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getTimestampAnnotationID(int timestampAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getTimestampAnnotationNamespace(int timestampAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public Timestamp getTimestampAnnotationValue(int timestampAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TransmittanceRange property storage
	//
	// Indexes: {u'Filter': [u'int instrumentIndex', u'int filterIndex']}
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	public PositiveInteger getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public NonNegativeInteger getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public PositiveInteger getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public NonNegativeInteger getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	//
	// UUID property storage
	//
	// Indexes: {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getUUIDFileName(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	//
	// Union property storage
	//
	// Indexes: {u'ROI': [u'int ROIIndex']}
	// {u'ROI': {u'OME': None}}
	// Is multi path? False

	// Ignoring Shape element, complex property
	//
	// Well property storage
	//
	// Indexes: {u'Plate': [u'int plateIndex', u'int wellIndex']}
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public String getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex)
	{
		return null;
	}

	public Color getWellColor(int plateIndex, int wellIndex)
	{
		return null;
	}

	public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex)
	{
		return null;
	}

	public String getWellExternalDescription(int plateIndex, int wellIndex)
	{
		return null;
	}

	public String getWellExternalIdentifier(int plateIndex, int wellIndex)
	{
		return null;
	}

	public String getWellID(int plateIndex, int wellIndex)
	{
		return null;
	}

	// Ignoring Plate_BackReference back reference
	public String getWellReagentRef(int plateIndex, int wellIndex)
	{
		return null;
	}

	public NonNegativeInteger getWellRow(int plateIndex, int wellIndex)
	{
		return null;
	}

	public String getWellType(int plateIndex, int wellIndex)
	{
		return null;
	}

	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// Indexes: {u'Well': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex']}
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	public String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	// Ignoring PlateAcquisition_BackReference back reference
	public Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	public Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	public Timestamp getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	// Ignoring Well_BackReference back reference
	//
	// WellSampleRef property storage
	//
	// Indexes: {u'PlateAcquisition': [u'int plateIndex', u'int plateAcquisitionIndex', u'int wellSampleRefIndex']}
	// {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference WellSampleRef

	//
	// XMLAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int XMLAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getXMLAnnotationAnnotationRef(int XMLAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getXMLAnnotationDescription(int XMLAnnotationIndex)
	{
		return null;
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getXMLAnnotationID(int XMLAnnotationIndex)
	{
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getXMLAnnotationNamespace(int XMLAnnotationIndex)
	{
		return null;
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public String getXMLAnnotationValue(int XMLAnnotationIndex)
	{
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference

	// -- Entity storage (manual definitions) --

	public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex)
	{
	}
	
	public void setMaskBinData(byte[] binData, int ROIIndex, int shapeIndex)
	{
	}

	// -- Entity storage (code generated definitions) --

	/** Sets the UUID associated with this collection of metadata. */
	public void setUUID(String uuid)
	{
	}

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
	public void setArcID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Instrument_BackReference accessor from parent LightSource
	public void setArcInstrument_BackReference(String instrument_BackReference, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Manufacturer accessor from parent LightSource
	public void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Model accessor from parent LightSource
	public void setArcModel(String model, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Power accessor from parent LightSource
	public void setArcPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
	}

	// SerialNumber accessor from parent LightSource
	public void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex)
	{
	}

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	public void setBinaryFileFileName(String fileName, int fileAnnotationIndex)
	{
	}

	public void setBinaryFileMIMEType(String mimeType, int fileAnnotationIndex)
	{
	}

	public void setBinaryFileSize(NonNegativeLong size, int fileAnnotationIndex)
	{
	}

	//
	// BinaryOnly property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setBinaryOnlyMetadataFile(String metadataFile)
	{
	}

	public void setBinaryOnlyUUID(String uuid)
	{
	}

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setBooleanAnnotationAnnotationRef(String annotation, int booleanAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setBooleanAnnotationDescription(String description, int booleanAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setBooleanAnnotationID(String id, int booleanAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Channel property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex)
	{
	}

	public void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex)
	{
	}

	public void setChannelColor(Color color, int imageIndex, int channelIndex)
	{
	}

	public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex)
	{
	}

	// Ignoring DetectorSettings element, complex property
	public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex)
	{
	}

	public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex)
	{
	}

	public void setChannelFilterSetRef(String filterSet, int imageIndex, int channelIndex)
	{
	}

	public void setChannelFluor(String fluor, int imageIndex, int channelIndex)
	{
	}

	public void setChannelID(String id, int imageIndex, int channelIndex)
	{
	}

	public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex)
	{
	}

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex)
	{
	}

	public void setChannelName(String name, int imageIndex, int channelIndex)
	{
	}

	public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex)
	{
	}

	public void setChannelSamplesPerPixel(PositiveInteger samplesPerPixel, int imageIndex, int channelIndex)
	{
	}

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setCommentAnnotationAnnotationRef(String annotation, int commentAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setCommentAnnotationDescription(String description, int commentAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setCommentAnnotationID(String id, int commentAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setCommentAnnotationNamespace(String namespace, int commentAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setCommentAnnotationValue(String value, int commentAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Dataset property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setDatasetAnnotationRef(String annotation, int datasetIndex, int annotationRefIndex)
	{
	}

	public void setDatasetDescription(String description, int datasetIndex)
	{
	}

	public void setDatasetExperimenterGroupRef(String experimenterGroup, int datasetIndex)
	{
	}

	public void setDatasetExperimenterRef(String experimenter, int datasetIndex)
	{
	}

	public void setDatasetID(String id, int datasetIndex)
	{
	}

	public void setDatasetImageRef(String image, int datasetIndex, int imageRefIndex)
	{
	}

	public void setDatasetName(String name, int datasetIndex)
	{
	}

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

	public void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorID(String id, int instrumentIndex, int detectorIndex)
	{
	}

	// Ignoring Instrument_BackReference back reference
	public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorModel(String model, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex)
	{
	}

	//
	// DetectorSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex)
	{
	}

	// Ignoring DetectorRef back reference
	public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsID(String id, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex)
	{
	}

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public void setDichroicID(String id, int instrumentIndex, int dichroicIndex)
	{
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex)
	{
	}

	public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex)
	{
	}

	public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex)
	{
	}

	public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex)
	{
	}

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

	public void setDoubleAnnotationAnnotationRef(String annotation, int doubleAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setDoubleAnnotationDescription(String description, int doubleAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setDoubleAnnotationID(String id, int doubleAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public void setEllipseFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setEllipseFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setEllipseFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setEllipseFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setEllipseFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setEllipseID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setEllipseLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setEllipseLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setEllipseStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setEllipseStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setEllipseText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setEllipseTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setEllipseTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setEllipseTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setEllipseTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setEllipseUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setEllipseVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setEllipseRadiusX(Double radiusX, int ROIIndex, int shapeIndex)
	{
	}

	public void setEllipseRadiusY(Double radiusY, int ROIIndex, int shapeIndex)
	{
	}

	public void setEllipseX(Double x, int ROIIndex, int shapeIndex)
	{
	}

	public void setEllipseY(Double y, int ROIIndex, int shapeIndex)
	{
	}

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

	public void setExperimentDescription(String description, int experimentIndex)
	{
	}

	public void setExperimentExperimenterRef(String experimenter, int experimentIndex)
	{
	}

	public void setExperimentID(String id, int experimentIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	public void setExperimentType(ExperimentType type, int experimentIndex)
	{
	}

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

	public void setExperimenterAnnotationRef(String annotation, int experimenterIndex, int annotationRefIndex)
	{
	}

	// Ignoring Dataset_BackReference back reference
	public void setExperimenterEmail(String email, int experimenterIndex)
	{
	}

	// Ignoring Experiment_BackReference back reference
	// Ignoring ExperimenterGroup_BackReference back reference
	public void setExperimenterFirstName(String firstName, int experimenterIndex)
	{
	}

	public void setExperimenterID(String id, int experimenterIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setExperimenterInstitution(String institution, int experimenterIndex)
	{
	}

	public void setExperimenterLastName(String lastName, int experimenterIndex)
	{
	}

	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setExperimenterMiddleName(String middleName, int experimenterIndex)
	{
	}

	// Ignoring Project_BackReference back reference
	public void setExperimenterUserName(String userName, int experimenterIndex)
	{
	}

	//
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setExperimenterGroupAnnotationRef(String annotation, int experimenterGroupIndex, int annotationRefIndex)
	{
	}

	// Ignoring Dataset_BackReference back reference
	public void setExperimenterGroupDescription(String description, int experimenterGroupIndex)
	{
	}

	public void setExperimenterGroupExperimenterRef(String experimenter, int experimenterGroupIndex, int experimenterRefIndex)
	{
	}

	public void setExperimenterGroupID(String id, int experimenterGroupIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setExperimenterGroupLeader(String leader, int experimenterGroupIndex, int leaderIndex)
	{
	}

	public void setExperimenterGroupName(String name, int experimenterGroupIndex)
	{
	}

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
	public void setFilamentID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Instrument_BackReference accessor from parent LightSource
	public void setFilamentInstrument_BackReference(String instrument_BackReference, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Manufacturer accessor from parent LightSource
	public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Model accessor from parent LightSource
	public void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Power accessor from parent LightSource
	public void setFilamentPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
	}

	// SerialNumber accessor from parent LightSource
	public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex)
	{
	}

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setFileAnnotationAnnotationRef(String annotation, int fileAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setFileAnnotationDescription(String description, int fileAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setFileAnnotationID(String id, int fileAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex)
	{
	}

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
	public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
	{
	}

	public void setFilterID(String id, int instrumentIndex, int filterIndex)
	{
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
	{
	}

	public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
	{
	}

	public void setFilterModel(String model, int instrumentIndex, int filterIndex)
	{
	}

	public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex)
	{
	}

	// Ignoring TransmittanceRange element, complex property
	public void setFilterType(FilterType type, int instrumentIndex, int filterIndex)
	{
	}

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	public void setFilterSetDichroicRef(String dichroic, int instrumentIndex, int filterSetIndex)
	{
	}

	public void setFilterSetEmissionFilterRef(String emissionFilter, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
	}

	public void setFilterSetExcitationFilterRef(String excitationFilter, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
	}

	public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex)
	{
	}

	// Ignoring Instrument_BackReference back reference
	public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
	{
	}

	public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
	{
	}

	public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
	{
	}

	public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex)
	{
	}

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

	public void setImageAcquisitionDate(Timestamp acquisitionDate, int imageIndex)
	{
	}

	public void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
	{
	}

	// Ignoring Dataset_BackReference back reference
	public void setImageDescription(String description, int imageIndex)
	{
	}

	public void setImageExperimentRef(String experiment, int imageIndex)
	{
	}

	public void setImageExperimenterGroupRef(String experimenterGroup, int imageIndex)
	{
	}

	public void setImageExperimenterRef(String experimenter, int imageIndex)
	{
	}

	public void setImageID(String id, int imageIndex)
	{
	}

	// Ignoring ImagingEnvironment element, complex property
	public void setImageInstrumentRef(String instrument, int imageIndex)
	{
	}

	public void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex)
	{
	}

	public void setImageName(String name, int imageIndex)
	{
	}

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	public void setImageROIRef(String roi, int imageIndex, int ROIRefIndex)
	{
	}

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

	public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex)
	{
	}

	public void setImagingEnvironmentCO2Percent(PercentFraction co2Percent, int imageIndex)
	{
	}

	public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex)
	{
	}

	public void setImagingEnvironmentTemperature(Double temperature, int imageIndex)
	{
	}

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	// Ignoring Detector element, complex property
	// Ignoring Dichroic element, complex property
	// Ignoring Filter element, complex property
	// Ignoring FilterSet element, complex property
	public void setInstrumentID(String id, int instrumentIndex)
	{
	}

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
	public void setLabelFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setLabelFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setLabelFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setLabelFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setLabelFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setLabelID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setLabelLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setLabelLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setLabelStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setLabelStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setLabelStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setLabelText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setLabelTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setLabelTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setLabelTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setLabelTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setLabelUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setLabelVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setLabelX(Double x, int ROIIndex, int shapeIndex)
	{
	}

	public void setLabelY(Double y, int ROIIndex, int shapeIndex)
	{
	}

	//
	// Laser property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	// ID accessor from parent LightSource
	public void setLaserID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Instrument_BackReference accessor from parent LightSource
	public void setLaserInstrument_BackReference(String instrument_BackReference, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Manufacturer accessor from parent LightSource
	public void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Model accessor from parent LightSource
	public void setLaserModel(String model, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Power accessor from parent LightSource
	public void setLaserPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
	}

	// SerialNumber accessor from parent LightSource
	public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserPulse(Pulse pulse, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserPump(String pump, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserType(LaserType type, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int lightSourceIndex)
	{
	}

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
	public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Instrument_BackReference accessor from parent LightSource
	public void setLightEmittingDiodeInstrument_BackReference(String instrument_BackReference, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Manufacturer accessor from parent LightSource
	public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Model accessor from parent LightSource
	public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Power accessor from parent LightSource
	public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightSourceIndex)
	{
	}

	// SerialNumber accessor from parent LightSource
	public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setLightPathDichroicRef(String dichroic, int imageIndex, int channelIndex)
	{
	}

	public void setLightPathEmissionFilterRef(String emissionFilter, int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
	}

	public void setLightPathExcitationFilterRef(String excitationFilter, int imageIndex, int channelIndex, int excitationFilterRefIndex)
	{
	}

	//
	// LightSourceSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	public void setChannelLightSourceSettingsAttenuation(PercentFraction attenuation, int imageIndex, int channelIndex)
	{
	}

	public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
	}

	public void setChannelLightSourceSettingsID(String id, int imageIndex, int channelIndex)
	{
	}

	public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
	}

	// Ignoring LightSourceRef back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setChannelLightSourceSettingsWavelength(PositiveInteger wavelength, int imageIndex, int channelIndex)
	{
	}

	public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
	}

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public void setLineFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setLineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setLineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setLineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setLineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setLineID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setLineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setLineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setLineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setLineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setLineText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setLineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setLineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setLineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setLineTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setLineUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setLineVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setLineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex)
	{
	}

	public void setLineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex)
	{
	}

	public void setLineX1(Double x1, int ROIIndex, int shapeIndex)
	{
	}

	public void setLineX2(Double x2, int ROIIndex, int shapeIndex)
	{
	}

	public void setLineY1(Double y1, int ROIIndex, int shapeIndex)
	{
	}

	public void setLineY2(Double y2, int ROIIndex, int shapeIndex)
	{
	}

	//
	// ListAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setListAnnotationAnnotationRef(String annotation, int listAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setListAnnotationDescription(String description, int listAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setListAnnotationID(String id, int listAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setListAnnotationNamespace(String namespace, int listAnnotationIndex)
	{
	}

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

	public void setLongAnnotationAnnotationRef(String annotation, int longAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setLongAnnotationDescription(String description, int longAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setLongAnnotationID(String id, int longAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setLongAnnotationValue(Long value, int longAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public void setMaskFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setMaskFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setMaskFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setMaskFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setMaskFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setMaskID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setMaskLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setMaskLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setMaskStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setMaskStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setMaskText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setMaskTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setMaskTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setMaskTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setMaskTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setMaskUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setMaskVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring BinData element, complex property
	public void setMaskHeight(Double height, int ROIIndex, int shapeIndex)
	{
	}

	public void setMaskWidth(Double width, int ROIIndex, int shapeIndex)
	{
	}

	public void setMaskX(Double x, int ROIIndex, int shapeIndex)
	{
	}

	public void setMaskY(Double y, int ROIIndex, int shapeIndex)
	{
	}

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

	public void setMicrobeamManipulationDescription(String description, int experimentIndex, int microbeamManipulationIndex)
	{
	}

	// Ignoring Experiment_BackReference back reference
	public void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex)
	{
	}

	public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	public void setMicrobeamManipulationROIRef(String roi, int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
	}

	public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex)
	{
	}

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

	public void setMicroscopeLotNumber(String lotNumber, int instrumentIndex)
	{
	}

	public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex)
	{
	}

	public void setMicroscopeModel(String model, int instrumentIndex)
	{
	}

	public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex)
	{
	}

	public void setMicroscopeType(MicroscopeType type, int instrumentIndex)
	{
	}

	//
	// Objective property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex)
	{
	}

	// Ignoring Instrument_BackReference back reference
	public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveNominalMagnification(PositiveInteger nominalMagnification, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex)
	{
	}

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex)
	{
	}

	public void setObjectiveSettingsID(String id, int imageIndex)
	{
	}

	public void setObjectiveSettingsMedium(Medium medium, int imageIndex)
	{
	}

	// Ignoring ObjectiveRef back reference
	public void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex)
	{
	}

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setPixelsAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
	{
	}

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex)
	{
	}

	public void setPixelsID(String id, int imageIndex)
	{
	}

	// Ignoring MetadataOnly element, complex property
	public void setPixelsPhysicalSizeX(PositiveFloat physicalSizeX, int imageIndex)
	{
	}

	public void setPixelsPhysicalSizeY(PositiveFloat physicalSizeY, int imageIndex)
	{
	}

	public void setPixelsPhysicalSizeZ(PositiveFloat physicalSizeZ, int imageIndex)
	{
	}

	// Ignoring Plane element, complex property
	public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex)
	{
	}

	public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex)
	{
	}

	public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex)
	{
	}

	public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex)
	{
	}

	public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex)
	{
	}

	// Ignoring TiffData element, complex property
	public void setPixelsTimeIncrement(Double timeIncrement, int imageIndex)
	{
	}

	public void setPixelsType(PixelType type, int imageIndex)
	{
	}

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setPlaneAnnotationRef(String annotation, int imageIndex, int planeIndex, int annotationRefIndex)
	{
	}

	public void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex)
	{
	}

	public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex)
	{
	}

	public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneTheC(NonNegativeInteger theC, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneTheT(NonNegativeInteger theT, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneTheZ(NonNegativeInteger theZ, int imageIndex, int planeIndex)
	{
	}

	//
	// Plate property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setPlateAnnotationRef(String annotation, int plateIndex, int annotationRefIndex)
	{
	}

	public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex)
	{
	}

	public void setPlateColumns(PositiveInteger columns, int plateIndex)
	{
	}

	public void setPlateDescription(String description, int plateIndex)
	{
	}

	public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
	{
	}

	public void setPlateFieldIndex(NonNegativeInteger fieldIndex, int plateIndex)
	{
	}

	public void setPlateID(String id, int plateIndex)
	{
	}

	public void setPlateName(String name, int plateIndex)
	{
	}

	// Ignoring PlateAcquisition element, complex property
	public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex)
	{
	}

	public void setPlateRows(PositiveInteger rows, int plateIndex)
	{
	}

	// Ignoring Screen_BackReference back reference
	public void setPlateStatus(String status, int plateIndex)
	{
	}

	// Ignoring Well element, complex property
	public void setPlateWellOriginX(Double wellOriginX, int plateIndex)
	{
	}

	public void setPlateWellOriginY(Double wellOriginY, int plateIndex)
	{
	}

	//
	// PlateAcquisition property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public void setPlateAcquisitionAnnotationRef(String annotation, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
	{
	}

	public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex)
	{
	}

	public void setPlateAcquisitionEndTime(Timestamp endTime, int plateIndex, int plateAcquisitionIndex)
	{
	}

	public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex)
	{
	}

	public void setPlateAcquisitionMaximumFieldCount(PositiveInteger maximumFieldCount, int plateIndex, int plateAcquisitionIndex)
	{
	}

	public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex)
	{
	}

	// Ignoring Plate_BackReference back reference
	public void setPlateAcquisitionStartTime(Timestamp startTime, int plateIndex, int plateAcquisitionIndex)
	{
	}

	public void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
	{
	}

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
	public void setPointFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setPointFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setPointFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setPointFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setPointFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setPointID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setPointLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setPointLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setPointStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setPointStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setPointText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setPointTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setPointTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setPointTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setPointTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setPointUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setPointVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setPointX(Double x, int ROIIndex, int shapeIndex)
	{
	}

	public void setPointY(Double y, int ROIIndex, int shapeIndex)
	{
	}

	//
	// Polygon property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public void setPolygonFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setPolygonFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setPolygonFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setPolygonFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setPolygonFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setPolygonID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setPolygonLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setPolygonLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setPolygonStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolygonStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setPolygonStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setPolygonText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setPolygonTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setPolygonTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setPolygonTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setPolygonTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setPolygonUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setPolygonVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setPolygonPoints(String points, int ROIIndex, int shapeIndex)
	{
	}

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public void setPolylineFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setPolylineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setPolylineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setPolylineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setPolylineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setPolylineID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setPolylineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setPolylineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setPolylineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setPolylineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setPolylineText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setPolylineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setPolylineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setPolylineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setPolylineTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setPolylineUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setPolylineVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setPolylineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex)
	{
	}

	public void setPolylineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex)
	{
	}

	public void setPolylinePoints(String points, int ROIIndex, int shapeIndex)
	{
	}

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setProjectAnnotationRef(String annotation, int projectIndex, int annotationRefIndex)
	{
	}

	public void setProjectDatasetRef(String dataset, int projectIndex, int datasetRefIndex)
	{
	}

	public void setProjectDescription(String description, int projectIndex)
	{
	}

	public void setProjectExperimenterGroupRef(String experimenterGroup, int projectIndex)
	{
	}

	public void setProjectExperimenterRef(String experimenter, int projectIndex)
	{
	}

	public void setProjectID(String id, int projectIndex)
	{
	}

	public void setProjectName(String name, int projectIndex)
	{
	}

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

	public void setROIAnnotationRef(String annotation, int ROIIndex, int annotationRefIndex)
	{
	}

	public void setROIDescription(String description, int ROIIndex)
	{
	}

	public void setROIID(String id, int ROIIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setROIName(String name, int ROIIndex)
	{
	}

	public void setROINamespace(String namespace, int ROIIndex)
	{
	}

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

	public void setReagentAnnotationRef(String annotation, int screenIndex, int reagentIndex, int annotationRefIndex)
	{
	}

	public void setReagentDescription(String description, int screenIndex, int reagentIndex)
	{
	}

	public void setReagentID(String id, int screenIndex, int reagentIndex)
	{
	}

	public void setReagentName(String name, int screenIndex, int reagentIndex)
	{
	}

	public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex)
	{
	}

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
	public void setRectangleFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
	}

	// FillRule accessor from parent Shape
	public void setRectangleFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
	}

	// FontFamily accessor from parent Shape
	public void setRectangleFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
	}

	// FontSize accessor from parent Shape
	public void setRectangleFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
	}

	// FontStyle accessor from parent Shape
	public void setRectangleFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
	}

	// ID accessor from parent Shape
	public void setRectangleID(String id, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setRectangleLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setRectangleLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setRectangleStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setRectangleStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
	}

	// Text accessor from parent Shape
	public void setRectangleText(String text, int ROIIndex, int shapeIndex)
	{
	}

	// TheC accessor from parent Shape
	public void setRectangleTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
	}

	// TheT accessor from parent Shape
	public void setRectangleTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
	}

	// TheZ accessor from parent Shape
	public void setRectangleTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
	}

	// Transform accessor from parent Shape
	public void setRectangleTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
	}

	// Union_BackReference accessor from parent Shape
	public void setRectangleUnion_BackReference(String union_BackReference, int ROIIndex, int shapeIndex)
	{
	}

	// Visible accessor from parent Shape
	public void setRectangleVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

	public void setRectangleHeight(Double height, int ROIIndex, int shapeIndex)
	{
	}

	public void setRectangleWidth(Double width, int ROIIndex, int shapeIndex)
	{
	}

	public void setRectangleX(Double x, int ROIIndex, int shapeIndex)
	{
	}

	public void setRectangleY(Double y, int ROIIndex, int shapeIndex)
	{
	}

	//
	// Screen property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setScreenAnnotationRef(String annotation, int screenIndex, int annotationRefIndex)
	{
	}

	public void setScreenDescription(String description, int screenIndex)
	{
	}

	public void setScreenID(String id, int screenIndex)
	{
	}

	public void setScreenName(String name, int screenIndex)
	{
	}

	public void setScreenPlateRef(String plate, int screenIndex, int plateRefIndex)
	{
	}

	public void setScreenProtocolDescription(String protocolDescription, int screenIndex)
	{
	}

	public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex)
	{
	}

	// Ignoring Reagent element, complex property
	public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex)
	{
	}

	public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex)
	{
	}

	public void setScreenType(String type, int screenIndex)
	{
	}

	//
	// StageLabel property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setStageLabelName(String name, int imageIndex)
	{
	}

	public void setStageLabelX(Double x, int imageIndex)
	{
	}

	public void setStageLabelY(Double y, int imageIndex)
	{
	}

	public void setStageLabelZ(Double z, int imageIndex)
	{
	}

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

	public void setTagAnnotationAnnotationRef(String annotation, int tagAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTagAnnotationDescription(String description, int tagAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTagAnnotationID(String id, int tagAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setTagAnnotationNamespace(String namespace, int tagAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setTagAnnotationValue(String value, int tagAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TermAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setTermAnnotationAnnotationRef(String annotation, int termAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTermAnnotationDescription(String description, int termAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTermAnnotationID(String id, int termAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setTermAnnotationNamespace(String namespace, int termAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setTermAnnotationValue(String value, int termAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setTiffDataFirstC(NonNegativeInteger firstC, int imageIndex, int tiffDataIndex)
	{
	}

	public void setTiffDataFirstT(NonNegativeInteger firstT, int imageIndex, int tiffDataIndex)
	{
	}

	public void setTiffDataFirstZ(NonNegativeInteger firstZ, int imageIndex, int tiffDataIndex)
	{
	}

	public void setTiffDataIFD(NonNegativeInteger ifd, int imageIndex, int tiffDataIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	public void setTiffDataPlaneCount(NonNegativeInteger planeCount, int imageIndex, int tiffDataIndex)
	{
	}

	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setTimestampAnnotationAnnotationRef(String annotation, int timestampAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTimestampAnnotationDescription(String description, int timestampAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTimestampAnnotationID(String id, int timestampAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setTimestampAnnotationValue(Timestamp value, int timestampAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	public void setTransmittanceRangeCutIn(PositiveInteger cutIn, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeCutInTolerance(NonNegativeInteger cutInTolerance, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeCutOut(PositiveInteger cutOut, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeCutOutTolerance(NonNegativeInteger cutOutTolerance, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex)
	{
	}

	//
	// UUID property storage
	//
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex)
	{
	}

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

	public void setWellAnnotationRef(String annotation, int plateIndex, int wellIndex, int annotationRefIndex)
	{
	}

	public void setWellColor(Color color, int plateIndex, int wellIndex)
	{
	}

	public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex)
	{
	}

	public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex)
	{
	}

	public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex)
	{
	}

	public void setWellID(String id, int plateIndex, int wellIndex)
	{
	}

	// Ignoring Plate_BackReference back reference
	public void setWellReagentRef(String reagent, int plateIndex, int wellIndex)
	{
	}

	public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex)
	{
	}

	public void setWellType(String type, int plateIndex, int wellIndex)
	{
	}

	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	public void setWellSampleAnnotationRef(String annotation, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex)
	{
	}

	public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	// Ignoring PlateAcquisition_BackReference back reference
	public void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSampleTimepoint(Timestamp timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

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

	public void setXMLAnnotationAnnotationRef(String annotation, int XMLAnnotationIndex, int annotationRefIndex)
	{
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setXMLAnnotationDescription(String description, int XMLAnnotationIndex)
	{
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setXMLAnnotationID(String id, int XMLAnnotationIndex)
	{
	}

	// Ignoring Image_BackReference back reference
	public void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex)
	{
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring StructuredAnnotations_BackReference back reference
	public void setXMLAnnotationValue(String value, int XMLAnnotationIndex)
	{
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
