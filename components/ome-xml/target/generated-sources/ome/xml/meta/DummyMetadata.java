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

/**
 * A dummy implementation for {@link MetadataStore} and
 * {@link MetadataRetrieve} that is used when no other
 * metadata implementations are available.
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

	public MetadataRoot getRoot()
	{
		return null;
	}

	public void setRoot(MetadataRoot root)
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

  public int getMapAnnotationAnnotationCount(int mapAnnotationIndex) {
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
	public int getLightSourceAnnotationRefCount(int instrumentIndex, int lightSourceIndex)
	{
		return -1;
	}

	public int getInstrumentAnnotationRefCount(int instrumentIndex)
	{
		return -1;
	}

	public int getObjectiveAnnotationRefCount(int instrumentIndex, int objectiveIndex)
	{
		return -1;
	}

	public int getDetectorAnnotationRefCount(int instrumentIndex, int detectorIndex)
	{
		return -1;
	}

	public int getChannelAnnotationRefCount(int imageIndex, int channelIndex)
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

	public int getScreenAnnotationRefCount(int screenIndex)
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

	public int getDichroicAnnotationRefCount(int instrumentIndex, int dichroicIndex)
	{
		return -1;
	}

	public int getWellAnnotationRefCount(int plateIndex, int wellIndex)
	{
		return -1;
	}

	public int getFilterAnnotationRefCount(int instrumentIndex, int filterIndex)
	{
		return -1;
	}

	public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		return -1;
	}

	public int getROIAnnotationRefCount(int ROIIndex)
	{
		return -1;
	}

	public int getProjectAnnotationRefCount(int projectIndex)
	{
		return -1;
	}

	public int getLightPathAnnotationRefCount(int imageIndex, int channelIndex)
	{
		return -1;
	}

	public int getImageAnnotationRefCount(int imageIndex)
	{
		return -1;
	}

	public int getDatasetAnnotationRefCount(int datasetIndex)
	{
		return -1;
	}

	public int getShapeAnnotationRefCount(int ROIIndex, int shapeIndex)
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
	// GenericExcitationSource entity counting
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

	// Map entity counting
	// MapAnnotation entity counting
	public int getMapAnnotationCount()
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
	// Rights entity counting
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
	// {u'TiffData': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
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

	/** Gets the UUID associated with this collection of metadata. */
	public String getUUID()
	{
		return null;
	}

	/** Gets the Map value associated with this annotation */
	public java.util.List<ome.xml.model.MapPair> getMapAnnotationValue(int mapAnnotationIndex)
	{
		return null;
	}

	/** Gets the Map value associated with this generic light source */
	public java.util.List<ome.xml.model.MapPair> getGenericExcitationSourceMap(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	/** Gets the Map value associated with this imaging environment */
	public java.util.List<ome.xml.model.MapPair> getImagingEnvironmentMap(int imageIndex)
	{
		return null;
	}

	// -- Entity retrieval (code generated definitions) --

	//
	// AnnotationRef property storage
	//
	// Indexes: {u'LightSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Objective': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'objectiveIndex', 'argtype': 'int', 'type': u'Objective'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Detector': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'detectorIndex', 'argtype': 'int', 'type': u'Detector'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Plate': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'ExperimenterGroup': [{'argname': u'experimenterGroupIndex', 'argtype': 'int', 'type': u'ExperimenterGroup'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Screen': [{'argname': u'screenIndex', 'argtype': 'int', 'type': u'Screen'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Reagent': [{'argname': u'screenIndex', 'argtype': 'int', 'type': u'Screen'}, {'argname': u'reagentIndex', 'argtype': 'int', 'type': u'Reagent'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Plane': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'planeIndex', 'argtype': 'int', 'type': u'Plane'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Experimenter': [{'argname': u'experimenterIndex', 'argtype': 'int', 'type': u'Experimenter'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Annotation': [{'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Dichroic': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'dichroicIndex', 'argtype': 'int', 'type': u'Dichroic'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Well': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'wellIndex', 'argtype': 'int', 'type': u'Well'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Project': [{'argname': u'projectIndex', 'argtype': 'int', 'type': u'Project'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'PlateAcquisition': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'plateAcquisitionIndex', 'argtype': 'int', 'type': u'PlateAcquisition'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'ROI': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Filter': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterIndex', 'argtype': 'int', 'type': u'Filter'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'LightPath': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Dataset': [{'argname': u'datasetIndex', 'argtype': 'int', 'type': u'Dataset'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}], u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}, {'argname': u'annotationRefIndex', 'argtype': 'int', 'type': u'AnnotationRef'}]}
	// {u'LightSource': {u'Instrument': {u'OME': None}}, u'Instrument': {u'OME': None}, u'Objective': {u'Instrument': {u'OME': None}}, u'Detector': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Screen': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'Dichroic': {u'Instrument': {u'OME': None}}, u'Well': {u'Plate': {u'OME': None}}, u'Filter': {u'Instrument': {u'OME': None}}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'ROI': {u'OME': None}, u'Project': {u'OME': None}, u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? True

	//
	// Arc property storage
	//
	// Indexes: {u'LightSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}]}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public String getArcAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
		return null;
	}

	// ID accessor from parent LightSource
	public String getArcID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

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
	public Power getArcPower(int instrumentIndex, int lightSourceIndex)
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
	// Indexes: {u'FileAnnotation': [{'argname': u'fileAnnotationIndex', 'argtype': 'int', 'type': u'FileAnnotation'}]}
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

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

	public String getBinaryOnlyMetadataFile()
	{
		return null;
	}

	public String getBinaryOnlyUUID()
	{
		return null;
	}

	//
	// BooleanAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'booleanAnnotationIndex', 'argtype': 'int', 'type': u'BooleanAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getBooleanAnnotationAnnotationRef(int booleanAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getBooleanAnnotationAnnotator(int booleanAnnotationIndex)
	{
		return null;
	}

	public String getBooleanAnnotationDescription(int booleanAnnotationIndex)
	{
		return null;
	}

	public String getBooleanAnnotationID(int booleanAnnotationIndex)
	{
		return null;
	}

	public String getBooleanAnnotationNamespace(int booleanAnnotationIndex)
	{
		return null;
	}

	public Boolean getBooleanAnnotationValue(int booleanAnnotationIndex)
	{
		return null;
	}

	//
	// Channel property storage
	//
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
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

	public Length getChannelEmissionWavelength(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Length getChannelExcitationWavelength(int imageIndex, int channelIndex)
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

	public Double getChannelNDFilter(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getChannelName(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Length getChannelPinholeSize(int imageIndex, int channelIndex)
	{
		return null;
	}

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
	// Indexes: {u'StructuredAnnotations': [{'argname': u'commentAnnotationIndex', 'argtype': 'int', 'type': u'CommentAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getCommentAnnotationAnnotationRef(int commentAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getCommentAnnotationAnnotator(int commentAnnotationIndex)
	{
		return null;
	}

	public String getCommentAnnotationDescription(int commentAnnotationIndex)
	{
		return null;
	}

	public String getCommentAnnotationID(int commentAnnotationIndex)
	{
		return null;
	}

	public String getCommentAnnotationNamespace(int commentAnnotationIndex)
	{
		return null;
	}

	public String getCommentAnnotationValue(int commentAnnotationIndex)
	{
		return null;
	}

	//
	// Dataset property storage
	//
	// Indexes: {u'OME': [{'argname': u'datasetIndex', 'argtype': 'int', 'type': u'Dataset'}]}
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

	//
	// DatasetRef property storage
	//
	// Indexes: {u'Project': [{'argname': u'projectIndex', 'argtype': 'int', 'type': u'Project'}, {'argname': u'datasetRefIndex', 'argtype': 'int', 'type': u'DatasetRef'}]}
	// {u'Project': {u'OME': None}}
	// Is multi path? False

	//
	// Detector property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'detectorIndex', 'argtype': 'int', 'type': u'Detector'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex)
	{
		return null;
	}

	public String getDetectorAnnotationRef(int instrumentIndex, int detectorIndex, int annotationRefIndex)
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

	public ElectricPotential getDetectorVoltage(int instrumentIndex, int detectorIndex)
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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getDetectorSettingsGain(int imageIndex, int channelIndex)
	{
		return null;
	}

	public String getDetectorSettingsID(int imageIndex, int channelIndex)
	{
		return null;
	}

	public PositiveInteger getDetectorSettingsIntegration(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getDetectorSettingsOffset(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Frequency getDetectorSettingsReadOutRate(int imageIndex, int channelIndex)
	{
		return null;
	}

	public ElectricPotential getDetectorSettingsVoltage(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Double getDetectorSettingsZoom(int imageIndex, int channelIndex)
	{
		return null;
	}

	//
	// Dichroic property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'dichroicIndex', 'argtype': 'int', 'type': u'Dichroic'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public String getDichroicAnnotationRef(int instrumentIndex, int dichroicIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getDichroicID(int instrumentIndex, int dichroicIndex)
	{
		return null;
	}

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
	// Indexes: {u'LightPath': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}], u'FilterSet': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterSetIndex', 'argtype': 'int', 'type': u'FilterSet'}]}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// DoubleAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'doubleAnnotationIndex', 'argtype': 'int', 'type': u'DoubleAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getDoubleAnnotationAnnotationRef(int doubleAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getDoubleAnnotationAnnotator(int doubleAnnotationIndex)
	{
		return null;
	}

	public String getDoubleAnnotationDescription(int doubleAnnotationIndex)
	{
		return null;
	}

	public String getDoubleAnnotationID(int doubleAnnotationIndex)
	{
		return null;
	}

	public String getDoubleAnnotationNamespace(int doubleAnnotationIndex)
	{
		return null;
	}

	public Double getDoubleAnnotationValue(int doubleAnnotationIndex)
	{
		return null;
	}

	//
	// Ellipse property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getEllipseAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getEllipseFontSize(int ROIIndex, int shapeIndex)
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
	public Length getEllipseStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'LightPath': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}, {'argname': u'emissionFilterRefIndex', 'argtype': 'int', 'type': u'EmissionFilterRef'}], u'FilterSet': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterSetIndex', 'argtype': 'int', 'type': u'FilterSet'}, {'argname': u'emissionFilterRefIndex', 'argtype': 'int', 'type': u'EmissionFilterRef'}]}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// ExcitationFilterRef property storage
	//
	// Indexes: {u'LightPath': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}, {'argname': u'excitationFilterRefIndex', 'argtype': 'int', 'type': u'ExcitationFilterRef'}], u'FilterSet': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterSetIndex', 'argtype': 'int', 'type': u'FilterSet'}, {'argname': u'excitationFilterRefIndex', 'argtype': 'int', 'type': u'ExcitationFilterRef'}]}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	//
	// Experiment property storage
	//
	// Indexes: {u'OME': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}]}
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

	public ExperimentType getExperimentType(int experimentIndex)
	{
		return null;
	}

	//
	// ExperimentRef property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	//
	// Experimenter property storage
	//
	// Indexes: {u'OME': [{'argname': u'experimenterIndex', 'argtype': 'int', 'type': u'Experimenter'}]}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getExperimenterEmail(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterFirstName(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterID(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterInstitution(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterLastName(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterMiddleName(int experimenterIndex)
	{
		return null;
	}

	public String getExperimenterUserName(int experimenterIndex)
	{
		return null;
	}

	//
	// ExperimenterGroup property storage
	//
	// Indexes: {u'OME': [{'argname': u'experimenterGroupIndex', 'argtype': 'int', 'type': u'ExperimenterGroup'}]}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimenterGroupAnnotationRef(int experimenterGroupIndex, int annotationRefIndex)
	{
		return null;
	}

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

	public String getExperimenterGroupLeader(int experimenterGroupIndex, int leaderIndex)
	{
		return null;
	}

	public String getExperimenterGroupName(int experimenterGroupIndex)
	{
		return null;
	}

	//
	// ExperimenterGroupRef property storage
	//
	// Indexes: {u'Project': [{'argname': u'projectIndex', 'argtype': 'int', 'type': u'Project'}], u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}], u'Dataset': [{'argname': u'datasetIndex', 'argtype': 'int', 'type': u'Dataset'}]}
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	//
	// ExperimenterRef property storage
	//
	// Indexes: {u'ExperimenterGroup': [{'argname': u'experimenterGroupIndex', 'argtype': 'int', 'type': u'ExperimenterGroup'}, {'argname': u'experimenterRefIndex', 'argtype': 'int', 'type': u'ExperimenterRef'}], u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}], u'Dataset': [{'argname': u'datasetIndex', 'argtype': 'int', 'type': u'Dataset'}], u'Project': [{'argname': u'projectIndex', 'argtype': 'int', 'type': u'Project'}], u'Experiment': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}], u'MicrobeamManipulation': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}, {'argname': u'microbeamManipulationIndex', 'argtype': 'int', 'type': u'MicrobeamManipulation'}]}
	// {u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	//
	// Filament property storage
	//
	// Indexes: {u'LightSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}]}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public String getFilamentAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
		return null;
	}

	// ID accessor from parent LightSource
	public String getFilamentID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

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
	public Power getFilamentPower(int instrumentIndex, int lightSourceIndex)
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
	// Indexes: {u'StructuredAnnotations': [{'argname': u'fileAnnotationIndex', 'argtype': 'int', 'type': u'FileAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getFileAnnotationAnnotationRef(int fileAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getFileAnnotationAnnotator(int fileAnnotationIndex)
	{
		return null;
	}

	public String getFileAnnotationDescription(int fileAnnotationIndex)
	{
		return null;
	}

	public String getFileAnnotationID(int fileAnnotationIndex)
	{
		return null;
	}

	public String getFileAnnotationNamespace(int fileAnnotationIndex)
	{
		return null;
	}

	//
	// Filter property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterIndex', 'argtype': 'int', 'type': u'Filter'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public String getFilterAnnotationRef(int instrumentIndex, int filterIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getFilterFilterWheel(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public String getFilterID(int instrumentIndex, int filterIndex)
	{
		return null;
	}

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

	public FilterType getFilterType(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	//
	// FilterSet property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterSetIndex', 'argtype': 'int', 'type': u'FilterSet'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	//
	// GenericExcitationSource property storage
	//
	// Indexes: {u'LightSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}]}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public String getGenericExcitationSourceAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
		return null;
	}

	// ID accessor from parent LightSource
	public String getGenericExcitationSourceID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// LotNumber accessor from parent LightSource
	public String getGenericExcitationSourceLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getGenericExcitationSourceManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Model accessor from parent LightSource
	public String getGenericExcitationSourceModel(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// Power accessor from parent LightSource
	public Power getGenericExcitationSourcePower(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getGenericExcitationSourceSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	//
	// Image property storage
	//
	// Indexes: {u'OME': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
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

	public String getImageROIRef(int imageIndex, int ROIRefIndex)
	{
		return null;
	}

	//
	// ImageRef property storage
	//
	// Indexes: {u'WellSample': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'wellIndex', 'argtype': 'int', 'type': u'Well'}, {'argname': u'wellSampleIndex', 'argtype': 'int', 'type': u'WellSample'}], u'Dataset': [{'argname': u'datasetIndex', 'argtype': 'int', 'type': u'Dataset'}, {'argname': u'imageRefIndex', 'argtype': 'int', 'type': u'ImageRef'}]}
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	//
	// ImagingEnvironment property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public Pressure getImagingEnvironmentAirPressure(int imageIndex)
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

	public Temperature getImagingEnvironmentTemperature(int imageIndex)
	{
		return null;
	}

	//
	// Instrument property storage
	//
	// Indexes: {u'OME': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}]}
	// {u'OME': None}
	// Is multi path? False

	public String getInstrumentAnnotationRef(int instrumentIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getInstrumentID(int instrumentIndex)
	{
		return null;
	}

	//
	// InstrumentRef property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	//
	// Label property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getLabelAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getLabelFontSize(int ROIIndex, int shapeIndex)
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
	public Length getLabelStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'LightSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}]}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public String getLaserAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
		return null;
	}

	// ID accessor from parent LightSource
	public String getLaserID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

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
	public Power getLaserPower(int instrumentIndex, int lightSourceIndex)
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

	public Frequency getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
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

	public Length getLaserWavelength(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

	//
	// Leader property storage
	//
	// Indexes: {u'ExperimenterGroup': [{'argname': u'experimenterGroupIndex', 'argtype': 'int', 'type': u'ExperimenterGroup'}, {'argname': u'leaderIndex', 'argtype': 'int', 'type': u'Leader'}]}
	// {u'ExperimenterGroup': {u'OME': None}}
	// Is multi path? False

	//
	// LightEmittingDiode property storage
	//
	// Indexes: {u'LightSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}]}
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public String getLightEmittingDiodeAnnotationRef(int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
		return null;
	}

	// ID accessor from parent LightSource
	public String getLightEmittingDiodeID(int instrumentIndex, int lightSourceIndex)
	{
		return null;
	}

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
	public Power getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex)
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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getLightPathAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex)
	{
		return null;
	}

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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}], u'MicrobeamManipulation': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}, {'argname': u'microbeamManipulationIndex', 'argtype': 'int', 'type': u'MicrobeamManipulation'}, {'argname': u'lightSourceSettingsIndex', 'argtype': 'int', 'type': u'LightSourceSettings'}]}
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

	public Length getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex)
	{
		return null;
	}

	public Length getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		return null;
	}

	//
	// Line property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getLineAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getLineFontSize(int ROIIndex, int shapeIndex)
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
	public Length getLineStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'StructuredAnnotations': [{'argname': u'listAnnotationIndex', 'argtype': 'int', 'type': u'ListAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getListAnnotationAnnotator(int listAnnotationIndex)
	{
		return null;
	}

	public String getListAnnotationDescription(int listAnnotationIndex)
	{
		return null;
	}

	public String getListAnnotationID(int listAnnotationIndex)
	{
		return null;
	}

	public String getListAnnotationNamespace(int listAnnotationIndex)
	{
		return null;
	}

	//
	// LongAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'longAnnotationIndex', 'argtype': 'int', 'type': u'LongAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getLongAnnotationAnnotationRef(int longAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getLongAnnotationAnnotator(int longAnnotationIndex)
	{
		return null;
	}

	public String getLongAnnotationDescription(int longAnnotationIndex)
	{
		return null;
	}

	public String getLongAnnotationID(int longAnnotationIndex)
	{
		return null;
	}

	public String getLongAnnotationNamespace(int longAnnotationIndex)
	{
		return null;
	}

	public Long getLongAnnotationValue(int longAnnotationIndex)
	{
		return null;
	}

	//
	// Map property storage
	//
	// Indexes: {u'GenericExcitationSource': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}], u'ImagingEnvironment': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'GenericExcitationSource': {u'LightSource': {u'Instrument': {u'OME': None}}}, u'ImagingEnvironment': {u'Image': {u'OME': None}}}
	// Is multi path? True

	//
	// MapAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'mapAnnotationIndex', 'argtype': 'int', 'type': u'MapAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getMapAnnotationAnnotationRef(int mapAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getMapAnnotationAnnotator(int mapAnnotationIndex)
	{
		return null;
	}

	public String getMapAnnotationDescription(int mapAnnotationIndex)
	{
		return null;
	}

	public String getMapAnnotationID(int mapAnnotationIndex)
	{
		return null;
	}

	public String getMapAnnotationNamespace(int mapAnnotationIndex)
	{
		return null;
	}

	//
	// Mask property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getMaskAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getMaskFontSize(int ROIIndex, int shapeIndex)
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
	public Length getMaskStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	//
	// MicrobeamManipulation property storage
	//
	// Indexes: {u'Experiment': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}, {'argname': u'microbeamManipulationIndex', 'argtype': 'int', 'type': u'MicrobeamManipulation'}]}
	// {u'Experiment': {u'OME': None}}
	// Is multi path? False

	public String getMicrobeamManipulationDescription(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

	public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

	public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex)
	{
		return null;
	}

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
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'microbeamManipulationRefIndex', 'argtype': 'int', 'type': u'MicrobeamManipulationRef'}]}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	//
	// Microscope property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}]}
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
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'objectiveIndex', 'argtype': 'int', 'type': u'Objective'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public String getObjectiveAnnotationRef(int instrumentIndex, int objectiveIndex, int annotationRefIndex)
	{
		return null;
	}

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

	public Double getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	public Length getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
	{
		return null;
	}

	//
	// ObjectiveSettings property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
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

	public Double getObjectiveSettingsRefractiveIndex(int imageIndex)
	{
		return null;
	}

	//
	// Pixels property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public Boolean getPixelsBigEndian(int imageIndex)
	{
		return null;
	}

	public DimensionOrder getPixelsDimensionOrder(int imageIndex)
	{
		return null;
	}

	public String getPixelsID(int imageIndex)
	{
		return null;
	}

	public Boolean getPixelsInterleaved(int imageIndex)
	{
		return null;
	}

	public Length getPixelsPhysicalSizeX(int imageIndex)
	{
		return null;
	}

	public Length getPixelsPhysicalSizeY(int imageIndex)
	{
		return null;
	}

	public Length getPixelsPhysicalSizeZ(int imageIndex)
	{
		return null;
	}

	public PositiveInteger getPixelsSignificantBits(int imageIndex)
	{
		return null;
	}

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

	public Time getPixelsTimeIncrement(int imageIndex)
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
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'planeIndex', 'argtype': 'int', 'type': u'Plane'}]}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex)
	{
		return null;
	}

	public Time getPlaneDeltaT(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Time getPlaneExposureTime(int imageIndex, int planeIndex)
	{
		return null;
	}

	public String getPlaneHashSHA1(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Length getPlanePositionX(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Length getPlanePositionY(int imageIndex, int planeIndex)
	{
		return null;
	}

	public Length getPlanePositionZ(int imageIndex, int planeIndex)
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
	// Indexes: {u'OME': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}]}
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

	public NamingConvention getPlateRowNamingConvention(int plateIndex)
	{
		return null;
	}

	public PositiveInteger getPlateRows(int plateIndex)
	{
		return null;
	}

	public String getPlateStatus(int plateIndex)
	{
		return null;
	}

	public Length getPlateWellOriginX(int plateIndex)
	{
		return null;
	}

	public Length getPlateWellOriginY(int plateIndex)
	{
		return null;
	}

	//
	// PlateAcquisition property storage
	//
	// Indexes: {u'Plate': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'plateAcquisitionIndex', 'argtype': 'int', 'type': u'PlateAcquisition'}]}
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
	// Indexes: {u'Screen': [{'argname': u'screenIndex', 'argtype': 'int', 'type': u'Screen'}, {'argname': u'plateRefIndex', 'argtype': 'int', 'type': u'PlateRef'}]}
	// {u'Screen': {u'OME': None}}
	// Is multi path? False

	//
	// Point property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getPointAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getPointFontSize(int ROIIndex, int shapeIndex)
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
	public Length getPointStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getPolygonAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getPolygonFontSize(int ROIIndex, int shapeIndex)
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
	public Length getPolygonStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getPolylineAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getPolylineFontSize(int ROIIndex, int shapeIndex)
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
	public Length getPolylineStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Indexes: {u'OME': [{'argname': u'projectIndex', 'argtype': 'int', 'type': u'Project'}]}
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
	// Indexes: {u'Laser': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'lightSourceIndex', 'argtype': 'int', 'type': u'LightSource'}]}
	// {u'Laser': {u'LightSource': {u'Instrument': {u'OME': None}}}}
	// Is multi path? False

	//
	// ROI property storage
	//
	// Indexes: {u'OME': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}]}
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

	public String getROIName(int ROIIndex)
	{
		return null;
	}

	public String getROINamespace(int ROIIndex)
	{
		return null;
	}

	//
	// ROIRef property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'ROIRefIndex', 'argtype': 'int', 'type': u'ROIRef'}], u'MicrobeamManipulation': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}, {'argname': u'microbeamManipulationIndex', 'argtype': 'int', 'type': u'MicrobeamManipulation'}, {'argname': u'ROIRefIndex', 'argtype': 'int', 'type': u'ROIRef'}]}
	// {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	//
	// Reagent property storage
	//
	// Indexes: {u'Screen': [{'argname': u'screenIndex', 'argtype': 'int', 'type': u'Screen'}, {'argname': u'reagentIndex', 'argtype': 'int', 'type': u'Reagent'}]}
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

	//
	// ReagentRef property storage
	//
	// Indexes: {u'Well': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'wellIndex', 'argtype': 'int', 'type': u'Well'}]}
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	//
	// Rectangle property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getRectangleAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		return null;
	}

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
	public Length getRectangleFontSize(int ROIIndex, int shapeIndex)
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
	public Length getRectangleStrokeWidth(int ROIIndex, int shapeIndex)
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
	// Rights property storage
	//
	// Indexes: {u'OME': []}
	// {u'OME': None}
	// Is multi path? False

	public String getRightsRightsHeld()
	{
		return null;
	}

	public String getRightsRightsHolder()
	{
		return null;
	}

	//
	// Screen property storage
	//
	// Indexes: {u'OME': [{'argname': u'screenIndex', 'argtype': 'int', 'type': u'Screen'}]}
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
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public String getStageLabelName(int imageIndex)
	{
		return null;
	}

	public Length getStageLabelX(int imageIndex)
	{
		return null;
	}

	public Length getStageLabelY(int imageIndex)
	{
		return null;
	}

	public Length getStageLabelZ(int imageIndex)
	{
		return null;
	}

	//
	// StructuredAnnotations property storage
	//
	// Indexes: {u'OME': []}
	// {u'OME': None}
	// Is multi path? False

	//
	// TagAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'tagAnnotationIndex', 'argtype': 'int', 'type': u'TagAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getTagAnnotationAnnotationRef(int tagAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getTagAnnotationAnnotator(int tagAnnotationIndex)
	{
		return null;
	}

	public String getTagAnnotationDescription(int tagAnnotationIndex)
	{
		return null;
	}

	public String getTagAnnotationID(int tagAnnotationIndex)
	{
		return null;
	}

	public String getTagAnnotationNamespace(int tagAnnotationIndex)
	{
		return null;
	}

	public String getTagAnnotationValue(int tagAnnotationIndex)
	{
		return null;
	}

	//
	// TermAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'termAnnotationIndex', 'argtype': 'int', 'type': u'TermAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getTermAnnotationAnnotationRef(int termAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getTermAnnotationAnnotator(int termAnnotationIndex)
	{
		return null;
	}

	public String getTermAnnotationDescription(int termAnnotationIndex)
	{
		return null;
	}

	public String getTermAnnotationID(int termAnnotationIndex)
	{
		return null;
	}

	public String getTermAnnotationNamespace(int termAnnotationIndex)
	{
		return null;
	}

	public String getTermAnnotationValue(int termAnnotationIndex)
	{
		return null;
	}

	//
	// TiffData property storage
	//
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
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

	public NonNegativeInteger getTiffDataPlaneCount(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	//
	// TimestampAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'timestampAnnotationIndex', 'argtype': 'int', 'type': u'TimestampAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getTimestampAnnotationAnnotationRef(int timestampAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getTimestampAnnotationAnnotator(int timestampAnnotationIndex)
	{
		return null;
	}

	public String getTimestampAnnotationDescription(int timestampAnnotationIndex)
	{
		return null;
	}

	public String getTimestampAnnotationID(int timestampAnnotationIndex)
	{
		return null;
	}

	public String getTimestampAnnotationNamespace(int timestampAnnotationIndex)
	{
		return null;
	}

	public Timestamp getTimestampAnnotationValue(int timestampAnnotationIndex)
	{
		return null;
	}

	//
	// TransmittanceRange property storage
	//
	// Indexes: {u'Filter': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterIndex', 'argtype': 'int', 'type': u'Filter'}]}
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	public Length getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public Length getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public Length getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
	{
		return null;
	}

	public Length getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
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
	// Indexes: {u'TiffData': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getUUIDFileName(int imageIndex, int tiffDataIndex)
	{
		return null;
	}

	//
	// Union property storage
	//
	// Indexes: {u'ROI': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}]}
	// {u'ROI': {u'OME': None}}
	// Is multi path? False

	//
	// Well property storage
	//
	// Indexes: {u'Plate': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'wellIndex', 'argtype': 'int', 'type': u'Well'}]}
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

	//
	// WellSample property storage
	//
	// Indexes: {u'Well': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'wellIndex', 'argtype': 'int', 'type': u'Well'}, {'argname': u'wellSampleIndex', 'argtype': 'int', 'type': u'WellSample'}]}
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

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

	public Length getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	public Length getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	public Timestamp getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return null;
	}

	//
	// WellSampleRef property storage
	//
	// Indexes: {u'PlateAcquisition': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}, {'argname': u'plateAcquisitionIndex', 'argtype': 'int', 'type': u'PlateAcquisition'}, {'argname': u'wellSampleRefIndex', 'argtype': 'int', 'type': u'WellSampleRef'}]}
	// {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	//
	// XMLAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'XMLAnnotationIndex', 'argtype': 'int', 'type': u'XMLAnnotation'}]}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getXMLAnnotationAnnotationRef(int XMLAnnotationIndex, int annotationRefIndex)
	{
		return null;
	}

	public String getXMLAnnotationAnnotator(int XMLAnnotationIndex)
	{
		return null;
	}

	public String getXMLAnnotationDescription(int XMLAnnotationIndex)
	{
		return null;
	}

	public String getXMLAnnotationID(int XMLAnnotationIndex)
	{
		return null;
	}

	public String getXMLAnnotationNamespace(int XMLAnnotationIndex)
	{
		return null;
	}

	public String getXMLAnnotationValue(int XMLAnnotationIndex)
	{
		return null;
	}


	// -- Entity storage (manual definitions) --

	public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex)
	{
	}

	public void setMaskBinData(byte[] binData, int ROIIndex, int shapeIndex)
	{
	}

	/** Sets the UUID associated with this collection of metadata. */
	public void setUUID(String uuid)
	{
	}

	/** Sets the Map value associated with this annotation */
	public void setMapAnnotationValue(java.util.List<ome.xml.model.MapPair> value, int mapAnnotationIndex)
	{
	}

	/** Sets the Map value associated with this generic light source */
	public void setGenericExcitationSourceMap(java.util.List<ome.xml.model.MapPair> map, int instrumentIndex, int lightSourceIndex)
	{
	}

	/** Sets the Map value associated with this imaging environment */
	public void setImagingEnvironmentMap(java.util.List<ome.xml.model.MapPair> map, int imageIndex)
	{
	}

	// -- Entity storage (code generated definitions) --

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
	public void setArcAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
	}

	// ID accessor from parent LightSource
	public void setArcID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

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
	public void setArcPower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setBooleanAnnotationAnnotator(String annotator, int booleanAnnotationIndex)
	{
	}

	public void setBooleanAnnotationDescription(String description, int booleanAnnotationIndex)
	{
	}

	public void setBooleanAnnotationID(String id, int booleanAnnotationIndex)
	{
	}

	public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex)
	{
	}

	public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex)
	{
	}

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

	public void setChannelEmissionWavelength(Length emissionWavelength, int imageIndex, int channelIndex)
	{
	}

	public void setChannelExcitationWavelength(Length excitationWavelength, int imageIndex, int channelIndex)
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

	public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex)
	{
	}

	public void setChannelName(String name, int imageIndex, int channelIndex)
	{
	}

	public void setChannelPinholeSize(Length pinholeSize, int imageIndex, int channelIndex)
	{
	}

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

	public void setCommentAnnotationAnnotator(String annotator, int commentAnnotationIndex)
	{
	}

	public void setCommentAnnotationDescription(String description, int commentAnnotationIndex)
	{
	}

	public void setCommentAnnotationID(String id, int commentAnnotationIndex)
	{
	}

	public void setCommentAnnotationNamespace(String namespace, int commentAnnotationIndex)
	{
	}

	public void setCommentAnnotationValue(String value, int commentAnnotationIndex)
	{
	}

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

	public void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorAnnotationRef(String annotation, int instrumentIndex, int detectorIndex, int annotationRefIndex)
	{
	}

	public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex)
	{
	}

	public void setDetectorID(String id, int instrumentIndex, int detectorIndex)
	{
	}

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

	public void setDetectorVoltage(ElectricPotential voltage, int instrumentIndex, int detectorIndex)
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

	public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsID(String id, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsIntegration(PositiveInteger integration, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsReadOutRate(Frequency readOutRate, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsVoltage(ElectricPotential voltage, int imageIndex, int channelIndex)
	{
	}

	public void setDetectorSettingsZoom(Double zoom, int imageIndex, int channelIndex)
	{
	}

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setDichroicAnnotationRef(String annotation, int instrumentIndex, int dichroicIndex, int annotationRefIndex)
	{
	}

	public void setDichroicID(String id, int instrumentIndex, int dichroicIndex)
	{
	}

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

	//
	// DoubleAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setDoubleAnnotationAnnotationRef(String annotation, int doubleAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setDoubleAnnotationAnnotator(String annotator, int doubleAnnotationIndex)
	{
	}

	public void setDoubleAnnotationDescription(String description, int doubleAnnotationIndex)
	{
	}

	public void setDoubleAnnotationID(String id, int doubleAnnotationIndex)
	{
	}

	public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex)
	{
	}

	public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex)
	{
	}

	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setEllipseAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setEllipseFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setEllipseLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setEllipseLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setEllipseStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setEllipseStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	public void setExperimentType(ExperimentType type, int experimentIndex)
	{
	}

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

	public void setExperimenterAnnotationRef(String annotation, int experimenterIndex, int annotationRefIndex)
	{
	}

	public void setExperimenterEmail(String email, int experimenterIndex)
	{
	}

	public void setExperimenterFirstName(String firstName, int experimenterIndex)
	{
	}

	public void setExperimenterID(String id, int experimenterIndex)
	{
	}

	public void setExperimenterInstitution(String institution, int experimenterIndex)
	{
	}

	public void setExperimenterLastName(String lastName, int experimenterIndex)
	{
	}

	public void setExperimenterMiddleName(String middleName, int experimenterIndex)
	{
	}

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

	public void setExperimenterGroupDescription(String description, int experimenterGroupIndex)
	{
	}

	public void setExperimenterGroupExperimenterRef(String experimenter, int experimenterGroupIndex, int experimenterRefIndex)
	{
	}

	public void setExperimenterGroupID(String id, int experimenterGroupIndex)
	{
	}

	public void setExperimenterGroupLeader(String leader, int experimenterGroupIndex, int leaderIndex)
	{
	}

	public void setExperimenterGroupName(String name, int experimenterGroupIndex)
	{
	}

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
	public void setFilamentAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
	}

	// ID accessor from parent LightSource
	public void setFilamentID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

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
	public void setFilamentPower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setFileAnnotationAnnotator(String annotator, int fileAnnotationIndex)
	{
	}

	public void setFileAnnotationDescription(String description, int fileAnnotationIndex)
	{
	}

	public void setFileAnnotationID(String id, int fileAnnotationIndex)
	{
	}

	public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex)
	{
	}

	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setFilterAnnotationRef(String annotation, int instrumentIndex, int filterIndex, int annotationRefIndex)
	{
	}

	public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
	{
	}

	public void setFilterID(String id, int instrumentIndex, int filterIndex)
	{
	}

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

	public void setFilterType(FilterType type, int instrumentIndex, int filterIndex)
	{
	}

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

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

	//
	// GenericExcitationSource property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public void setGenericExcitationSourceAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
	}

	// ID accessor from parent LightSource
	public void setGenericExcitationSourceID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

	// LotNumber accessor from parent LightSource
	public void setGenericExcitationSourceLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Manufacturer accessor from parent LightSource
	public void setGenericExcitationSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Model accessor from parent LightSource
	public void setGenericExcitationSourceModel(String model, int instrumentIndex, int lightSourceIndex)
	{
	}

	// Power accessor from parent LightSource
	public void setGenericExcitationSourcePower(Power power, int instrumentIndex, int lightSourceIndex)
	{
	}

	// SerialNumber accessor from parent LightSource
	public void setGenericExcitationSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
	}

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

	public void setImageInstrumentRef(String instrument, int imageIndex)
	{
	}

	public void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex)
	{
	}

	public void setImageName(String name, int imageIndex)
	{
	}

	public void setImageROIRef(String roi, int imageIndex, int ROIRefIndex)
	{
	}

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

	public void setImagingEnvironmentAirPressure(Pressure airPressure, int imageIndex)
	{
	}

	public void setImagingEnvironmentCO2Percent(PercentFraction co2Percent, int imageIndex)
	{
	}

	public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex)
	{
	}

	public void setImagingEnvironmentTemperature(Temperature temperature, int imageIndex)
	{
	}

	//
	// Instrument property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setInstrumentAnnotationRef(String annotation, int instrumentIndex, int annotationRefIndex)
	{
	}

	public void setInstrumentID(String id, int instrumentIndex)
	{
	}

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
	public void setLabelAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setLabelFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setLabelLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setLabelLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setLabelStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setLabelStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setLabelStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// AnnotationRef accessor from parent LightSource
	public void setLaserAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
	}

	// ID accessor from parent LightSource
	public void setLaserID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

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
	public void setLaserPower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setLaserRepetitionRate(Frequency repetitionRate, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserType(LaserType type, int instrumentIndex, int lightSourceIndex)
	{
	}

	public void setLaserWavelength(Length wavelength, int instrumentIndex, int lightSourceIndex)
	{
	}

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
	public void setLightEmittingDiodeAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
	}

	// ID accessor from parent LightSource
	public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex)
	{
	}

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
	public void setLightEmittingDiodePower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setLightPathAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex)
	{
	}

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

	public void setChannelLightSourceSettingsWavelength(Length wavelength, int imageIndex, int channelIndex)
	{
	}

	public void setMicrobeamManipulationLightSourceSettingsWavelength(Length wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
	}

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setLineAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setLineFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setLineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setLineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setLineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setLineStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	public void setListAnnotationAnnotator(String annotator, int listAnnotationIndex)
	{
	}

	public void setListAnnotationDescription(String description, int listAnnotationIndex)
	{
	}

	public void setListAnnotationID(String id, int listAnnotationIndex)
	{
	}

	public void setListAnnotationNamespace(String namespace, int listAnnotationIndex)
	{
	}

	//
	// LongAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setLongAnnotationAnnotationRef(String annotation, int longAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setLongAnnotationAnnotator(String annotator, int longAnnotationIndex)
	{
	}

	public void setLongAnnotationDescription(String description, int longAnnotationIndex)
	{
	}

	public void setLongAnnotationID(String id, int longAnnotationIndex)
	{
	}

	public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex)
	{
	}

	public void setLongAnnotationValue(Long value, int longAnnotationIndex)
	{
	}

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

	public void setMapAnnotationAnnotationRef(String annotation, int mapAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setMapAnnotationAnnotator(String annotator, int mapAnnotationIndex)
	{
	}

	public void setMapAnnotationDescription(String description, int mapAnnotationIndex)
	{
	}

	public void setMapAnnotationID(String id, int mapAnnotationIndex)
	{
	}

	public void setMapAnnotationNamespace(String namespace, int mapAnnotationIndex)
	{
	}

	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setMaskAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setMaskFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setMaskLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setMaskLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setMaskStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setMaskStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setMaskVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
	}

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

	public void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex)
	{
	}

	public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex)
	{
	}

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

	public void setObjectiveAnnotationRef(String annotation, int instrumentIndex, int objectiveIndex, int annotationRefIndex)
	{
	}

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

	public void setObjectiveNominalMagnification(Double nominalMagnification, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
	{
	}

	public void setObjectiveWorkingDistance(Length workingDistance, int instrumentIndex, int objectiveIndex)
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

	public void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex)
	{
	}

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setPixelsBigEndian(Boolean bigEndian, int imageIndex)
	{
	}

	public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex)
	{
	}

	public void setPixelsID(String id, int imageIndex)
	{
	}

	public void setPixelsInterleaved(Boolean interleaved, int imageIndex)
	{
	}

	public void setPixelsPhysicalSizeX(Length physicalSizeX, int imageIndex)
	{
	}

	public void setPixelsPhysicalSizeY(Length physicalSizeY, int imageIndex)
	{
	}

	public void setPixelsPhysicalSizeZ(Length physicalSizeZ, int imageIndex)
	{
	}

	public void setPixelsSignificantBits(PositiveInteger significantBits, int imageIndex)
	{
	}

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

	public void setPixelsTimeIncrement(Time timeIncrement, int imageIndex)
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

	public void setPlaneDeltaT(Time deltaT, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneExposureTime(Time exposureTime, int imageIndex, int planeIndex)
	{
	}

	public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex)
	{
	}

	public void setPlanePositionX(Length positionX, int imageIndex, int planeIndex)
	{
	}

	public void setPlanePositionY(Length positionY, int imageIndex, int planeIndex)
	{
	}

	public void setPlanePositionZ(Length positionZ, int imageIndex, int planeIndex)
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

	public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex)
	{
	}

	public void setPlateRows(PositiveInteger rows, int plateIndex)
	{
	}

	public void setPlateStatus(String status, int plateIndex)
	{
	}

	public void setPlateWellOriginX(Length wellOriginX, int plateIndex)
	{
	}

	public void setPlateWellOriginY(Length wellOriginY, int plateIndex)
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

	//
	// Point property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setPointAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setPointFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setPointLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setPointLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setPointStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setPointStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// AnnotationRef accessor from parent Shape
	public void setPolygonAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setPolygonFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setPolygonLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setPolygonLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setPolygonStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolygonStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setPolygonStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// AnnotationRef accessor from parent Shape
	public void setPolylineAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setPolylineFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setPolylineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setPolylineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setPolylineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setPolylineStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	public void setROIName(String name, int ROIIndex)
	{
	}

	public void setROINamespace(String namespace, int ROIIndex)
	{
	}

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
	public void setRectangleAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
	}

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
	public void setRectangleFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// LineCap accessor from parent Shape
	public void setRectangleLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
	}

	// Locked accessor from parent Shape
	public void setRectangleLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeColor accessor from parent Shape
	public void setRectangleStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeDashArray accessor from parent Shape
	public void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
	}

	// StrokeWidth accessor from parent Shape
	public void setRectangleStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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
	// Rights property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setRightsRightsHeld(String rightsHeld)
	{
	}

	public void setRightsRightsHolder(String rightsHolder)
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

	public void setStageLabelX(Length x, int imageIndex)
	{
	}

	public void setStageLabelY(Length y, int imageIndex)
	{
	}

	public void setStageLabelZ(Length z, int imageIndex)
	{
	}

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

	public void setTagAnnotationAnnotationRef(String annotation, int tagAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setTagAnnotationAnnotator(String annotator, int tagAnnotationIndex)
	{
	}

	public void setTagAnnotationDescription(String description, int tagAnnotationIndex)
	{
	}

	public void setTagAnnotationID(String id, int tagAnnotationIndex)
	{
	}

	public void setTagAnnotationNamespace(String namespace, int tagAnnotationIndex)
	{
	}

	public void setTagAnnotationValue(String value, int tagAnnotationIndex)
	{
	}

	//
	// TermAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setTermAnnotationAnnotationRef(String annotation, int termAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setTermAnnotationAnnotator(String annotator, int termAnnotationIndex)
	{
	}

	public void setTermAnnotationDescription(String description, int termAnnotationIndex)
	{
	}

	public void setTermAnnotationID(String id, int termAnnotationIndex)
	{
	}

	public void setTermAnnotationNamespace(String namespace, int termAnnotationIndex)
	{
	}

	public void setTermAnnotationValue(String value, int termAnnotationIndex)
	{
	}

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

	public void setTiffDataPlaneCount(NonNegativeInteger planeCount, int imageIndex, int tiffDataIndex)
	{
	}

	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setTimestampAnnotationAnnotationRef(String annotation, int timestampAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setTimestampAnnotationAnnotator(String annotator, int timestampAnnotationIndex)
	{
	}

	public void setTimestampAnnotationDescription(String description, int timestampAnnotationIndex)
	{
	}

	public void setTimestampAnnotationID(String id, int timestampAnnotationIndex)
	{
	}

	public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex)
	{
	}

	public void setTimestampAnnotationValue(Timestamp value, int timestampAnnotationIndex)
	{
	}

	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	public void setTransmittanceRangeCutIn(Length cutIn, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeCutInTolerance(Length cutInTolerance, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeCutOut(Length cutOut, int instrumentIndex, int filterIndex)
	{
	}

	public void setTransmittanceRangeCutOutTolerance(Length cutOutTolerance, int instrumentIndex, int filterIndex)
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

	public void setWellReagentRef(String reagent, int plateIndex, int wellIndex)
	{
	}

	public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex)
	{
	}

	public void setWellType(String type, int plateIndex, int wellIndex)
	{
	}

	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSamplePositionX(Length positionX, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSamplePositionY(Length positionY, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

	public void setWellSampleTimepoint(Timestamp timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
	{
	}

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

	public void setXMLAnnotationAnnotationRef(String annotation, int XMLAnnotationIndex, int annotationRefIndex)
	{
	}

	public void setXMLAnnotationAnnotator(String annotator, int XMLAnnotationIndex)
	{
	}

	public void setXMLAnnotationDescription(String description, int XMLAnnotationIndex)
	{
	}

	public void setXMLAnnotationID(String id, int XMLAnnotationIndex)
	{
	}

	public void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex)
	{
	}

	public void setXMLAnnotationValue(String value, int XMLAnnotationIndex)
	{
	}

}
