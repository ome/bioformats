
/*
 * loci.formats.meta.OMEXMLMetadataImpl
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
 * Created by callan via xsd-fu on 2010-04-29 17:56:04+0100
 *
 *-----------------------------------------------------------------------------
 */

// TODO: TEMPORARY, WILL NOT BE USED AFTER TESTING IS COMPLETE
package ome.xml;

import ome.xml.r201004.*;
import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

/**
 * A metadata store implementation for constructing and manipulating OME-XML
 * DOMs for the 2010-04 version of OME-XML. It requires the
 * ome.xml.r201004 package to compile (part of ome-xml.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/ome/OMEXML200809Metadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/ome/OMEXML200809Metadata.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXMLMetadataImpl implements MetadataStore, MetadataRetrieve
{
	private OME root;

	private String uuid;

	public void createRoot()
	{
		root = new OME();
	}

	public Object getRoot()
	{
		return root;
	}

	public void setRoot(Object root)
	{
		if (!(root instanceof OME))
		{
			throw new IllegalArgumentException(
					"Expecting OME class or subclass.");
		}
		this.root = (OME) root;
	}

	// -- Entity counting --

	// AnnotationRef entity counting
	public int getPlateAnnotationRefCount(int plateIndex)
	{
		return 0;
	}

	public int getListAnnotationAnnotationRefCount(int listAnnotationIndex)
	{
		return 0;
	}

	public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		return 0;
	}

	public int getImageAnnotationRefCount(int imageIndex)
	{
		return 0;
	}

	public int getScreenAnnotationRefCount(int screenIndex)
	{
		return 0;
	}

	public int getWellAnnotationRefCount(int plateIndex, int wellIndex)
	{
		return 0;
	}

	public int getROIAnnotationRefCount(int ROIIndex)
	{
		return 0;
	}

	public int getDatasetAnnotationRefCount(int datasetIndex)
	{
		return 0;
	}

	public int getProjectAnnotationRefCount(int projectIndex)
	{
		return 0;
	}

	public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex)
	{
		return 0;
	}

	public int getShapeAnnotationRefCount(int ROIIndex, int shapeIndex)
	{
		return 0;
	}

	public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex)
	{
		return 0;
	}

	public int getExperimenterAnnotationRefCount(int experimenterIndex)
	{
		return 0;
	}

	public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		return 0;
	}

	public int getPixelsAnnotationRefCount(int imageIndex)
	{
		return 0;
	}

	public int getChannelAnnotationRefCount(int imageIndex, int channelIndex)
	{
		return 0;
	}

	// Arc entity counting
	// BinData entity counting
	public int getMaskBinDataCount(int ROIIndex, int shapeIndex)
	{
		return 0;
	}

	public int getPixelsBinDataCount(int imageIndex)
	{
		return 0;
	}

	// BinaryFile entity counting
	// BooleanAnnotation entity counting
	public int getBooleanAnnotationCount()
	{
		return 0;
	}

	// Channel entity counting
	public int getChannelCount(int imageIndex)
	{
		return 0;
	}

	// Contact entity counting
	// Dataset entity counting
	public int getDatasetCount()
	{
		return 0;
	}

	// DatasetRef entity counting
	public int getDatasetRefCount(int imageIndex)
	{
		return 0;
	}

	// Detector entity counting
	public int getDetectorCount(int instrumentIndex)
	{
		return 0;
	}

	// DetectorSettings entity counting
	// Dichroic entity counting
	public int getDichroicCount(int instrumentIndex)
	{
		return 0;
	}

	// DichroicRef entity counting
	// DoubleAnnotation entity counting
	public int getDoubleAnnotationCount()
	{
		return 0;
	}

	// Ellipse entity counting
	// EmissionFilterRef entity counting
	public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex)
	{
		return 0;
	}

	public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		return 0;
	}

	// ExcitationFilterRef entity counting
	public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex)
	{
		return 0;
	}

	public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		return 0;
	}

	// Experiment entity counting
	public int getExperimentCount()
	{
		return 0;
	}

	// ExperimentRef entity counting
	// Experimenter entity counting
	public int getExperimenterCount()
	{
		return 0;
	}

	// ExperimenterRef entity counting
	// External entity counting
	// Filament entity counting
	// FileAnnotation entity counting
	public int getFileAnnotationCount()
	{
		return 0;
	}

	// Filter entity counting
	public int getFilterCount(int instrumentIndex)
	{
		return 0;
	}

	// FilterSet entity counting
	public int getFilterSetCount(int instrumentIndex)
	{
		return 0;
	}

	// FilterSetRef entity counting
	// Group entity counting
	public int getGroupCount()
	{
		return 0;
	}

	// GroupRef entity counting
	public int getExperimenterGroupRefCount(int experimenterIndex)
	{
		return 0;
	}

	// Image entity counting
	public int getImageCount()
	{
		return 0;
	}

	// ImageRef entity counting
	// ImagingEnvironment entity counting
	// Instrument entity counting
	public int getInstrumentCount()
	{
		return 0;
	}

	// InstrumentRef entity counting
	// Laser entity counting
	// Leader entity counting
	// LightEmittingDiode entity counting
	// LightPath entity counting
	// LightSourceSettings entity counting
	public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex)
	{
		return 0;
	}

	// Line entity counting
	// ListAnnotation entity counting
	public int getListAnnotationCount()
	{
		return 0;
	}

	// LongAnnotation entity counting
	public int getLongAnnotationCount()
	{
		return 0;
	}

	// Mask entity counting
	// MetadataOnly entity counting
	// MicrobeamManipulation entity counting
	public int getMicrobeamManipulationCount(int experimentIndex)
	{
		return 0;
	}

	// MicrobeamManipulationRef entity counting
	public int getMicrobeamManipulationRefCount(int imageIndex)
	{
		return 0;
	}

	// Microscope entity counting
	// OTF entity counting
	public int getOTFCount(int instrumentIndex)
	{
		return 0;
	}

	// OTFRef entity counting
	// Objective entity counting
	public int getObjectiveCount(int instrumentIndex)
	{
		return 0;
	}

	// ObjectiveSettings entity counting
	// Path entity counting
	// Pixels entity counting
	// Plane entity counting
	public int getPlaneCount(int imageIndex)
	{
		return 0;
	}

	// Plate entity counting
	public int getPlateCount()
	{
		return 0;
	}

	// PlateAcquisition entity counting
	public int getPlateAcquisitionCount(int plateIndex)
	{
		return 0;
	}

	// PlateRef entity counting
	public int getPlateRefCount(int screenIndex)
	{
		return 0;
	}

	// Point entity counting
	// Polyline entity counting
	// Project entity counting
	public int getProjectCount()
	{
		return 0;
	}

	// ProjectRef entity counting
	public int getProjectRefCount(int datasetIndex)
	{
		return 0;
	}

	// Pump entity counting
	// ROI entity counting
	public int getROICount()
	{
		return 0;
	}

	// ROIRef entity counting
	public int getImageROIRefCount(int imageIndex)
	{
		return 0;
	}

	public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex)
	{
		return 0;
	}

	// Reagent entity counting
	public int getReagentCount(int screenIndex)
	{
		return 0;
	}

	// ReagentRef entity counting
	// Rectangle entity counting
	// Screen entity counting
	public int getScreenCount()
	{
		return 0;
	}

	// ScreenRef entity counting
	public int getScreenRefCount(int plateIndex)
	{
		return 0;
	}

	// StageLabel entity counting
	// StringAnnotation entity counting
	public int getStringAnnotationCount()
	{
		return 0;
	}

	// StructuredAnnotations entity counting
	// Text entity counting
	// TiffData entity counting
	public int getTiffDataCount(int imageIndex)
	{
		return 0;
	}

	// TimestampAnnotation entity counting
	public int getTimestampAnnotationCount()
	{
		return 0;
	}

	// TransmittanceRange entity counting
	// UUID entity counting
	// Union entity counting
	// Well entity counting
	public int getWellCount(int plateIndex)
	{
		return 0;
	}

	// WellSample entity counting
	public int getWellSampleCount(int plateIndex, int wellIndex)
	{
		return 0;
	}

	// WellSampleRef entity counting
	public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		return 0;
	}

	// XMLAnnotation entity counting
	public int getXMLAnnotationCount()
	{
		return 0;
	}


	// -- Entity retrieval --

	/** Gets the UUID associated with this collection of metadata. */
	public String getUUID()
	{
		return uuid;
	}

	//
	// AnnotationRef property storage
	//
	// Indexes: {u'Plate': [u'int plateIndex', u'int annotationRefIndex'], u'Reagent': [u'int screenIndex', u'int reagentIndex', u'int annotationRefIndex'], u'ListAnnotation': [u'int listAnnotationIndex', u'int annotationRefIndex'], u'Image': [u'int imageIndex', u'int annotationRefIndex'], u'Well': [u'int plateIndex', u'int wellIndex', u'int annotationRefIndex'], u'WellSample': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex', u'int annotationRefIndex'], u'Dataset': [u'int datasetIndex', u'int annotationRefIndex'], u'Project': [u'int projectIndex', u'int annotationRefIndex'], u'PlateAcquisition': [u'int plateIndex', u'int plateAcquisitionIndex', u'int annotationRefIndex'], u'Shape': [u'int ROIIndex', u'int shapeIndex', u'int annotationRefIndex'], u'Plane': [u'int imageIndex', u'int planeIndex', u'int annotationRefIndex'], u'Experimenter': [u'int experimenterIndex', u'int annotationRefIndex'], u'ROI': [u'int ROIIndex', u'int annotationRefIndex'], u'Pixels': [u'int imageIndex', u'int annotationRefIndex'], u'Screen': [u'int screenIndex', u'int annotationRefIndex'], u'Channel': [u'int imageIndex', u'int channelIndex', u'int annotationRefIndex']}
	// {u'Plate': {u'OME': None}, u'ListAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'ROI': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Shape': {u'Union': {u'ROI': {u'OME': None}}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
		// Parent: u'LightSource'
	}


	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getArcLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
		// Parent: u'LightSource'
	}


	// Manufacturer accessor from parent LightSource
	public String getArcManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
		// Parent: u'LightSource'
	}


	// Model accessor from parent LightSource
	public String getArcModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
		// Parent: u'LightSource'
	}


	// Power accessor from parent LightSource
	public Double getArcPower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
		// Parent: u'LightSource'
	}


	// SerialNumber accessor from parent LightSource
	public String getArcSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Type accessor
	public ArcType getArcType(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getType();
		// Parent: u'LightSource'
	}


	//
	// BinaryFile property storage
	//
	// Indexes: {u'FileAnnotation': [u'int fileAnnotationIndex'], u'OTF': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	// Indexes to iterate over: [(u'FileAnnotation', [u'int fileAnnotationIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// FileName accessor
	public String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
		// FileName is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getBinaryFile().getFileName();
		// Parent: u'FileAnnotation'
		// Parent: u'OTF'
	}


	// FileName accessor
	public String getOTFBinaryFileFileName(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
		// FileName is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getBinaryFile().getFileName();
		// Parent: u'FileAnnotation'
		// Parent: u'OTF'
	}


	// Indexes to iterate over: [(u'FileAnnotation', [u'int fileAnnotationIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// MIMEType accessor
	public String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
		// MIMEType is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getBinaryFile().getMIMEType();
		// Parent: u'FileAnnotation'
		// Parent: u'OTF'
	}


	// MIMEType accessor
	public String getOTFBinaryFileMIMEType(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
		// MIMEType is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getBinaryFile().getMIMEType();
		// Parent: u'FileAnnotation'
		// Parent: u'OTF'
	}


	// Indexes to iterate over: [(u'FileAnnotation', [u'int fileAnnotationIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// Size accessor
	public Integer getFileAnnotationBinaryFileSize(int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
		// Size is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getBinaryFile().getSize();
		// Parent: u'FileAnnotation'
		// Parent: u'OTF'
	}


	// Size accessor
	public Integer getOTFBinaryFileSize(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
		// Size is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getBinaryFile().getSize();
		// Parent: u'FileAnnotation'
		// Parent: u'OTF'
	}


	//
	// BooleanAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int booleanAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int booleanAnnotationIndex'])]
	// ID accessor
	public String getBooleanAnnotationID(int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int booleanAnnotationIndex'])]
	// Namespace accessor
	public String getBooleanAnnotationNamespace(int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int booleanAnnotationIndex'])]
	// Value accessor
	public Boolean getBooleanAnnotationValue(int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getValue();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Channel property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int channelIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// AcquisitionMode accessor
	public AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AcquisitionMode is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getAcquisitionMode();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// AnnotationRef accessor
	public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// Color accessor
	public Integer getChannelColor(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Color is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getColor();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// ContrastMethod accessor
	public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ContrastMethod is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getContrastMethod();
		// Parent: u'Pixels'
	}


	// Ignoring DetectorSettings element, complex property
	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// EmissionWavelength accessor
	public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// EmissionWavelength is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getEmissionWavelength();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// ExcitationWavelength accessor
	public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ExcitationWavelength is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getExcitationWavelength();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// FilterSetRef accessor
	public String getChannelFilterSetRef(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FilterSetRef is reference and occurs only once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLinkedFilterSet().getID();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// Fluor accessor
	public String getChannelFluor(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Fluor is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getFluor();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// ID accessor
	public String getChannelID(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getID();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// IlluminationType accessor
	public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// IlluminationType is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getIlluminationType();
		// Parent: u'Pixels'
	}


	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// NDFilter accessor
	public Double getChannelNDFilter(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// NDFilter is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getNDFilter();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// Name accessor
	public String getChannelName(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Name is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getName();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// OTFRef accessor
	public String getChannelOTFRef(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// OTFRef is reference and occurs only once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLinkedOTF().getID();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// PinholeSize accessor
	public Double getChannelPinholeSize(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PinholeSize is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getPinholeSize();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// PockelCellSetting accessor
	public Integer getChannelPockelCellSetting(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PockelCellSetting is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getPockelCellSetting();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int channelIndex'])]
	// SamplesPerPixel accessor
	public Integer getChannelSamplesPerPixel(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// SamplesPerPixel is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getSamplesPerPixel();
		// Parent: u'Pixels'
	}


	//
	// Contact property storage
	//
	// Indexes: {u'Group': [u'int groupIndex']}
	// {u'Group': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Contact

	//
	// Dataset property storage
	//
	// Indexes: {u'OME': [u'int datasetIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// AnnotationRef accessor
	public String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getDataset(datasetIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// Description accessor
	public String getDatasetDescription(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getDataset(datasetIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// ExperimenterRef accessor
	public String getDatasetExperimenterRef(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getDataset(datasetIndex).getLinkedExperimenter().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// GroupRef accessor
	public String getDatasetGroupRef(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// GroupRef is reference and occurs only once
		return root.getDataset(datasetIndex).getLinkedGroup().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// ID accessor
	public String getDatasetID(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getDataset(datasetIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring Image_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// Name accessor
	public String getDatasetName(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getDataset(datasetIndex).getName();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int datasetIndex'])]
	// ProjectRef accessor
	public String getDatasetProjectRef(int datasetIndex, int projectRefIndex)
	{
		// Parents: {u'OME': None}
		// ProjectRef is reference and occurs more than once
		return root.getDataset(datasetIndex).getLinkedProject(projectRefIndex).getID();
	}


	//
	// DatasetRef property storage
	//
	// Indexes: {u'Image': [u'int imageIndex', u'int datasetRefIndex']}
	// {u'Image': {u'OME': None}}
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

	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// AmplificationGain accessor
	public Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// AmplificationGain is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getAmplificationGain();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Gain accessor
	public Double getDetectorGain(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Gain is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getGain();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// ID accessor
	public String getDetectorID(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getID();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// LotNumber accessor
	public String getDetectorLotNumber(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getLotNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Manufacturer accessor
	public String getDetectorManufacturer(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getManufacturer();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Model accessor
	public String getDetectorModel(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getModel();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Offset accessor
	public Double getDetectorOffset(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Offset is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getOffset();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// SerialNumber accessor
	public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getSerialNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Type accessor
	public DetectorType getDetectorType(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getType();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Voltage accessor
	public Double getDetectorVoltage(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Voltage is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getVoltage();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int detectorIndex'])]
	// Zoom accessor
	public Double getDetectorZoom(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Zoom is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getZoom();
		// Parent: u'Instrument'
	}


	//
	// DetectorSettings property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// Binning accessor
	public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Binning is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getBinning();
		// Parent: u'Channel'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// Gain accessor
	public Double getDetectorSettingsGain(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Gain is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getGain();
		// Parent: u'Channel'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// ID accessor
	public String getDetectorSettingsID(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getID();
		// Parent: u'Channel'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// Offset accessor
	public Double getDetectorSettingsOffset(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Offset is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getOffset();
		// Parent: u'Channel'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// ReadOutRate accessor
	public Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ReadOutRate is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getReadOutRate();
		// Parent: u'Channel'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// Voltage accessor
	public Double getDetectorSettingsVoltage(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Voltage is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getVoltage();
		// Parent: u'Channel'
	}


	//
	// Dichroic property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int dichroicIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int dichroicIndex'])]
	// ID accessor
	public String getDichroicID(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getID();
		// Parent: u'Instrument'
	}


	// Ignoring LightPath_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int dichroicIndex'])]
	// LotNumber accessor
	public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getLotNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int dichroicIndex'])]
	// Manufacturer accessor
	public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getManufacturer();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int dichroicIndex'])]
	// Model accessor
	public String getDichroicModel(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getModel();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int dichroicIndex'])]
	// SerialNumber accessor
	public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getSerialNumber();
		// Parent: u'Instrument'
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

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int doubleAnnotationIndex'])]
	// ID accessor
	public String getDoubleAnnotationID(int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int doubleAnnotationIndex'])]
	// Namespace accessor
	public String getDoubleAnnotationNamespace(int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int doubleAnnotationIndex'])]
	// Value accessor
	public Double getDoubleAnnotationValue(int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getValue();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getEllipseDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getEllipseFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getEllipseFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getEllipseID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getEllipseLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getEllipseName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getEllipseStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getEllipseTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getEllipseTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getEllipseTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getEllipseTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// RadiusX accessor
	public Double getEllipseRadiusX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getRadiusX();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// RadiusY accessor
	public Double getEllipseRadiusY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getRadiusY();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X accessor
	public Double getEllipseX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y accessor
	public Double getEllipseY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
		// Parent: u'Shape'
	}


	//
	// EmissionFilterRef property storage
	//
	// Indexes: {u'LightPath': [u'int imageIndex', u'int channelIndex', u'int emissionFilterRefIndex'], u'FilterSet': [u'int instrumentIndex', u'int filterSetIndex', u'int emissionFilterRefIndex']}
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

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

	// Indexes to iterate over: [(u'OME', [u'int experimentIndex'])]
	// Description accessor
	public String getExperimentDescription(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getExperiment(experimentIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int experimentIndex'])]
	// ExperimenterRef accessor
	public String getExperimentExperimenterRef(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getExperiment(experimentIndex).getLinkedExperimenter().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int experimentIndex'])]
	// ID accessor
	public String getExperimentID(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getExperiment(experimentIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	// Indexes to iterate over: [(u'OME', [u'int experimentIndex'])]
	// Type accessor
	public ExperimentType getExperimentType(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// Type is not a reference
		return root.getExperiment(experimentIndex).getType();
		// Parent: u'OME'
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

	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// AnnotationRef accessor
	public String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getExperimenter(experimenterIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Ignoring Dataset_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// DisplayName accessor
	public String getExperimenterDisplayName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// DisplayName is not a reference
		return root.getExperimenter(experimenterIndex).getDisplayName();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// Email accessor
	public String getExperimenterEmail(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// Email is not a reference
		return root.getExperimenter(experimenterIndex).getEmail();
		// Parent: u'OME'
	}


	// Ignoring Experiment_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// FirstName accessor
	public String getExperimenterFirstName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// FirstName is not a reference
		return root.getExperimenter(experimenterIndex).getFirstName();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// GroupRef accessor
	public String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex)
	{
		// Parents: {u'OME': None}
		// GroupRef is reference and occurs more than once
		return root.getExperimenter(experimenterIndex).getLinkedGroup(groupRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// ID accessor
	public String getExperimenterID(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getExperimenter(experimenterIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring Image_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// Institution accessor
	public String getExperimenterInstitution(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// Institution is not a reference
		return root.getExperimenter(experimenterIndex).getInstitution();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// LastName accessor
	public String getExperimenterLastName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// LastName is not a reference
		return root.getExperimenter(experimenterIndex).getLastName();
		// Parent: u'OME'
	}


	// Ignoring MicrobeamManipulation_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// MiddleName accessor
	public String getExperimenterMiddleName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// MiddleName is not a reference
		return root.getExperimenter(experimenterIndex).getMiddleName();
		// Parent: u'OME'
	}


	// Ignoring Project_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int experimenterIndex'])]
	// UserName accessor
	public String getExperimenterUserName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// UserName is not a reference
		return root.getExperimenter(experimenterIndex).getUserName();
		// Parent: u'OME'
	}


	//
	// ExperimenterRef property storage
	//
	// Indexes: {u'Project': [u'int projectIndex'], u'MicrobeamManipulation': [u'int experimentIndex', u'int microbeamManipulationIndex'], u'Image': [u'int imageIndex'], u'Experiment': [u'int experimentIndex'], u'Dataset': [u'int datasetIndex']}
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
		// Parent: u'LightSource'
	}


	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
		// Parent: u'LightSource'
	}


	// Manufacturer accessor from parent LightSource
	public String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
		// Parent: u'LightSource'
	}


	// Model accessor from parent LightSource
	public String getFilamentModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
		// Parent: u'LightSource'
	}


	// Power accessor from parent LightSource
	public Double getFilamentPower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
		// Parent: u'LightSource'
	}


	// SerialNumber accessor from parent LightSource
	public String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Type accessor
	public FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getType();
		// Parent: u'LightSource'
	}


	//
	// FileAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int fileAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int fileAnnotationIndex'])]
	// ID accessor
	public String getFileAnnotationID(int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int fileAnnotationIndex'])]
	// Namespace accessor
	public String getFileAnnotationNamespace(int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Filter property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int filterIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// FilterWheel accessor
	public String getFilterFilterWheel(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// FilterWheel is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getFilterWheel();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// ID accessor
	public String getFilterID(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getID();
		// Parent: u'Instrument'
	}


	// Ignoring LightPath_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// LotNumber accessor
	public String getFilterLotNumber(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getLotNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// Manufacturer accessor
	public String getFilterManufacturer(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getManufacturer();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// Model accessor
	public String getFilterModel(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getModel();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// SerialNumber accessor
	public String getFilterSerialNumber(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getSerialNumber();
		// Parent: u'Instrument'
	}


	// Ignoring TransmittanceRange element, complex property
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterIndex'])]
	// Type accessor
	public FilterType getFilterType(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getType();
		// Parent: u'Instrument'
	}


	//
	// FilterSet property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int filterSetIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// DichroicRef accessor
	public String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// DichroicRef is reference and occurs only once
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLinkedDichroic().getID();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// EmissionFilterRef accessor
	public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// EmissionFilterRef is reference and occurs more than once
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLinkedEmissionFilter(emissionFilterRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// ExcitationFilterRef accessor
	public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ExcitationFilterRef is reference and occurs more than once
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLinkedExcitationFilter(excitationFilterRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// ID accessor
	public String getFilterSetID(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getID();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// LotNumber accessor
	public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLotNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// Manufacturer accessor
	public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getManufacturer();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// Model accessor
	public String getFilterSetModel(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getModel();
		// Parent: u'Instrument'
	}


	// Ignoring OTF_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int filterSetIndex'])]
	// SerialNumber accessor
	public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getSerialNumber();
		// Parent: u'Instrument'
	}


	//
	// FilterSetRef property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex'], u'OTF': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'OTF': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference FilterSetRef

	//
	// Group property storage
	//
	// Indexes: {u'OME': [u'int groupIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int groupIndex'])]
	// Contact accessor
	public String getGroupContact(int groupIndex)
	{
		// Parents: {u'OME': None}
		// Contact is reference and occurs only once
		return root.getGroup(groupIndex).getLinkedContact().getID();
		// Parent: u'OME'
	}


	// Ignoring Dataset_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int groupIndex'])]
	// Description accessor
	public String getGroupDescription(int groupIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getGroup(groupIndex).getDescription();
		// Parent: u'OME'
	}


	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int groupIndex'])]
	// ID accessor
	public String getGroupID(int groupIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getGroup(groupIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring Image_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int groupIndex'])]
	// Leader accessor
	public String getGroupLeader(int groupIndex)
	{
		// Parents: {u'OME': None}
		// Leader is reference and occurs only once
		return root.getGroup(groupIndex).getLinkedLeader().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int groupIndex'])]
	// Name accessor
	public String getGroupName(int groupIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getGroup(groupIndex).getName();
		// Parent: u'OME'
	}


	// Ignoring Project_BackReference back reference
	//
	// GroupRef property storage
	//
	// Indexes: {u'Project': [u'int projectIndex'], u'Image': [u'int imageIndex'], u'Experimenter': [u'int experimenterIndex', u'int groupRefIndex'], u'Dataset': [u'int datasetIndex']}
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Experimenter': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference GroupRef

	//
	// Image property storage
	//
	// Indexes: {u'OME': [u'int imageIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// AcquiredDate accessor
	public String getImageAcquiredDate(int imageIndex)
	{
		// Parents: {u'OME': None}
		// AcquiredDate is not a reference
		return root.getImage(imageIndex).getAcquiredDate();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// AnnotationRef accessor
	public String getImageAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// DatasetRef accessor
	public String getImageDatasetRef(int imageIndex, int datasetRefIndex)
	{
		// Parents: {u'OME': None}
		// DatasetRef is reference and occurs more than once
		return root.getImage(imageIndex).getLinkedDataset(datasetRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// Description accessor
	public String getImageDescription(int imageIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getImage(imageIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// ExperimentRef accessor
	public String getImageExperimentRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimentRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedExperiment().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// ExperimenterRef accessor
	public String getImageExperimenterRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedExperimenter().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// GroupRef accessor
	public String getImageGroupRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// GroupRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedGroup().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// ID accessor
	public String getImageID(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getImage(imageIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring ImagingEnvironment element, complex property
	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// InstrumentRef accessor
	public String getImageInstrumentRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// InstrumentRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedInstrument().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// MicrobeamManipulationRef accessor
	public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex)
	{
		// Parents: {u'OME': None}
		// MicrobeamManipulationRef is reference and occurs more than once
		return root.getImage(imageIndex).getLinkedMicrobeamManipulation(microbeamManipulationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// Name accessor
	public String getImageName(int imageIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getImage(imageIndex).getName();
		// Parent: u'OME'
	}


	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	// Indexes to iterate over: [(u'OME', [u'int imageIndex'])]
	// ROIRef accessor
	public String getImageROIRef(int imageIndex, int ROIRefIndex)
	{
		// Parents: {u'OME': None}
		// ROIRef is reference and occurs more than once
		return root.getImage(imageIndex).getLinkedROI(ROIRefIndex).getID();
	}


	// Ignoring StageLabel element, complex property
	// Ignoring WellSample_BackReference back reference
	//
	// ImageRef property storage
	//
	// Indexes: {u'WellSample': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex']}
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ImageRef

	//
	// ImagingEnvironment property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// AirPressure accessor
	public Double getImagingEnvironmentAirPressure(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// AirPressure is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getAirPressure();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// CO2Percent accessor
	public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// CO2Percent is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getCO2Percent();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// Humidity accessor
	public PercentFraction getImagingEnvironmentHumidity(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Humidity is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getHumidity();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// Temperature accessor
	public Double getImagingEnvironmentTemperature(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Temperature is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getTemperature();
		// Parent: u'Image'
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
	// Indexes to iterate over: [(u'OME', [u'int instrumentIndex'])]
	// ID accessor
	public String getInstrumentID(int instrumentIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring LightSource element, complex property
	// Ignoring Microscope element, complex property
	// Ignoring OTF element, complex property
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
		// Parent: u'LightSource'
	}


	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLaserLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
		// Parent: u'LightSource'
	}


	// Manufacturer accessor from parent LightSource
	public String getLaserManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
		// Parent: u'LightSource'
	}


	// Model accessor from parent LightSource
	public String getLaserModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
		// Parent: u'LightSource'
	}


	// Power accessor from parent LightSource
	public Double getLaserPower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
		// Parent: u'LightSource'
	}


	// SerialNumber accessor from parent LightSource
	public String getLaserSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// FrequencyMultiplication accessor
	public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getFrequencyMultiplication();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// LaserMedium accessor
	public LaserMedium getLaserLaserMedium(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLaserMedium();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// PockelCell accessor
	public Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPockelCell();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Pulse accessor
	public Pulse getLaserPulse(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPulse();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Pump accessor
	public String getLaserPump(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLinkedPump().getID();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// RepetitionRate accessor
	public Double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getRepetitionRate();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Tuneable accessor
	public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getTuneable();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Type accessor
	public LaserType getLaserType(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getType();
		// Parent: u'LightSource'
	}


	// Indexes to iterate over: [(u'LightSource', [u'int instrumentIndex', u'int lightSourceIndex'])]
	// Wavelength accessor
	public PositiveInteger getLaserWavelength(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getWavelength();
		// Parent: u'LightSource'
	}


	//
	// Leader property storage
	//
	// Indexes: {u'Group': [u'int groupIndex']}
	// {u'Group': {u'OME': None}}
	// Is multi path? False

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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
		// Parent: u'LightSource'
	}


	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
		// Parent: u'LightSource'
	}


	// Manufacturer accessor from parent LightSource
	public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
		// Parent: u'LightSource'
	}


	// Model accessor from parent LightSource
	public String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
		// Parent: u'LightSource'
	}


	// Power accessor from parent LightSource
	public Double getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
		// Parent: u'LightSource'
	}


	// SerialNumber accessor from parent LightSource
	public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
		// Parent: u'LightSource'
	}


	//
	// LightPath property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// DichroicRef accessor
	public String getLightPathDichroicRef(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// DichroicRef is reference and occurs only once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().getLinkedDichroic().getID();
		// Parent: u'Channel'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// EmissionFilterRef accessor
	public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// EmissionFilterRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().getLinkedEmissionFilter(emissionFilterRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex'])]
	// ExcitationFilterRef accessor
	public String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ExcitationFilterRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().getLinkedExcitationFilter(excitationFilterRefIndex).getID();
	}


	//
	// LightSourceSettings property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex'], u'MicrobeamManipulation': [u'int experimentIndex', u'int microbeamManipulationIndex', u'int lightSourceSettingsIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex']), (u'MicrobeamManipulation', [u'int experimentIndex', u'int microbeamManipulationIndex', u'int lightSourceSettingsIndex'])]
	// Attenuation accessor
	public PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Attenuation is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightSourceSettings().getAttenuation();
		// Parent: u'Channel'
		// Parent: u'MicrobeamManipulation'
	}


	// Attenuation accessor
	public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Attenuation is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLightSourceSettings(lightSourceSettingsIndex).getAttenuation();
		// Parent: u'Channel'
		// Parent: u'MicrobeamManipulation'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex']), (u'MicrobeamManipulation', [u'int experimentIndex', u'int microbeamManipulationIndex', u'int lightSourceSettingsIndex'])]
	// ID accessor
	public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightSourceSettings().getID();
		// Parent: u'Channel'
		// Parent: u'MicrobeamManipulation'
	}


	// ID accessor
	public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ID is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLightSourceSettings(lightSourceSettingsIndex).getID();
		// Parent: u'Channel'
		// Parent: u'MicrobeamManipulation'
	}


	// Indexes to iterate over: [(u'Channel', [u'int imageIndex', u'int channelIndex']), (u'MicrobeamManipulation', [u'int experimentIndex', u'int microbeamManipulationIndex', u'int lightSourceSettingsIndex'])]
	// Wavelength accessor
	public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Wavelength is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightSourceSettings().getWavelength();
		// Parent: u'Channel'
		// Parent: u'MicrobeamManipulation'
	}


	// Wavelength accessor
	public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Wavelength is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLightSourceSettings(lightSourceSettingsIndex).getWavelength();
		// Parent: u'Channel'
		// Parent: u'MicrobeamManipulation'
	}


	//
	// Line property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getLineDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getLineFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getLineFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getLineID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getLineLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getLineName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getLineStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getLineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getLineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getLineTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getLineTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getLineTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getLineTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X1 accessor
	public Double getLineX1(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX1();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X2 accessor
	public Double getLineX2(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX2();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y1 accessor
	public Double getLineY1(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY1();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y2 accessor
	public Double getLineY2(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY2();
		// Parent: u'Shape'
	}


	//
	// ListAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int listAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int listAnnotationIndex'])]
	// AnnotationRef accessor
	public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int listAnnotationIndex'])]
	// ID accessor
	public String getListAnnotationID(int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int listAnnotationIndex'])]
	// Namespace accessor
	public String getListAnnotationNamespace(int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// LongAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int longAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int longAnnotationIndex'])]
	// ID accessor
	public String getLongAnnotationID(int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int longAnnotationIndex'])]
	// Namespace accessor
	public String getLongAnnotationNamespace(int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int longAnnotationIndex'])]
	// Value accessor
	public Long getLongAnnotationValue(int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getValue();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getMaskDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getMaskFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getMaskFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getMaskID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getMaskLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getMaskName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getMaskStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getMaskStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getMaskStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getMaskTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getMaskTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getMaskTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getMaskTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Ignoring BinData element, complex property
	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X accessor
	public Double getMaskX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y accessor
	public Double getMaskY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
		// Parent: u'Shape'
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

	// Indexes to iterate over: [(u'Experiment', [u'int experimentIndex', u'int microbeamManipulationIndex'])]
	// ExperimenterRef accessor
	public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ExperimenterRef is reference and occurs only once
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLinkedExperimenter().getID();
		// Parent: u'Experiment'
	}


	// Indexes to iterate over: [(u'Experiment', [u'int experimentIndex', u'int microbeamManipulationIndex'])]
	// ID accessor
	public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ID is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getID();
		// Parent: u'Experiment'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	// Indexes to iterate over: [(u'Experiment', [u'int experimentIndex', u'int microbeamManipulationIndex'])]
	// ROIRef accessor
	public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ROIRef is reference and occurs more than once
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLinkedROI(ROIRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Experiment', [u'int experimentIndex', u'int microbeamManipulationIndex'])]
	// Type accessor
	public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// Type is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getType();
		// Parent: u'Experiment'
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

	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex'])]
	// LotNumber accessor
	public String getMicroscopeLotNumber(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getLotNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex'])]
	// Manufacturer accessor
	public String getMicroscopeManufacturer(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getManufacturer();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex'])]
	// Model accessor
	public String getMicroscopeModel(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getModel();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex'])]
	// SerialNumber accessor
	public String getMicroscopeSerialNumber(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getSerialNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex'])]
	// Type accessor
	public MicroscopeType getMicroscopeType(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getType();
		// Parent: u'Instrument'
	}


	//
	// OTF property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int OTFIndex'])]
	// FilterSetRef accessor
	public String getOTFFilterSetRef(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// FilterSetRef is reference and occurs only once
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getLinkedFilterSet().getID();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int OTFIndex'])]
	// ID accessor
	public String getOTFID(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getID();
		// Parent: u'Instrument'
	}


	// Ignoring ObjectiveSettings element, complex property
	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int OTFIndex'])]
	// OpticalAxisAveraged accessor
	public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// OpticalAxisAveraged is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getOpticalAxisAveraged();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int OTFIndex'])]
	// SizeX accessor
	public PositiveInteger getOTFSizeX(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SizeX is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getSizeX();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int OTFIndex'])]
	// SizeY accessor
	public PositiveInteger getOTFSizeY(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SizeY is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getSizeY();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int OTFIndex'])]
	// Type accessor
	public PixelType getOTFType(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getType();
		// Parent: u'Instrument'
	}


	//
	// OTFRef property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference OTFRef

	//
	// Objective property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int objectiveIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// CalibratedMagnification accessor
	public Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// CalibratedMagnification is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getCalibratedMagnification();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// Correction accessor
	public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Correction is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getCorrection();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// ID accessor
	public String getObjectiveID(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getID();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// Immersion accessor
	public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Immersion is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getImmersion();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// Iris accessor
	public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Iris is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getIris();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// LensNA accessor
	public Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LensNA is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getLensNA();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// LotNumber accessor
	public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getLotNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// Manufacturer accessor
	public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getManufacturer();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// Model accessor
	public String getObjectiveModel(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getModel();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// NominalMagnification accessor
	public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// NominalMagnification is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getNominalMagnification();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// SerialNumber accessor
	public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getSerialNumber();
		// Parent: u'Instrument'
	}


	// Indexes to iterate over: [(u'Instrument', [u'int instrumentIndex', u'int objectiveIndex'])]
	// WorkingDistance accessor
	public Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// WorkingDistance is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getWorkingDistance();
		// Parent: u'Instrument'
	}


	//
	// ObjectiveSettings property storage
	//
	// Indexes: {u'Image': [u'int imageIndex'], u'OTF': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Indexes to iterate over: [(u'Image', [u'int imageIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// CorrectionCollar accessor
	public Double getImageObjectiveSettingsCorrectionCollar(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// CorrectionCollar is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getCorrectionCollar();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// CorrectionCollar accessor
	public Double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// CorrectionCollar is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getObjectiveSettings().getCorrectionCollar();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// ID accessor
	public String getImageObjectiveSettingsID(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// ID is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getID();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// ID accessor
	public String getOTFObjectiveSettingsID(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getObjectiveSettings().getID();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// Medium accessor
	public Medium getImageObjectiveSettingsMedium(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// Medium is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getMedium();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// Medium accessor
	public Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// Medium is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getObjectiveSettings().getMedium();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex']), (u'OTF', [u'int instrumentIndex', u'int OTFIndex'])]
	// RefractiveIndex accessor
	public Double getImageObjectiveSettingsRefractiveIndex(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// RefractiveIndex is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getRefractiveIndex();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	// RefractiveIndex accessor
	public Double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int OTFIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
		// RefractiveIndex is not a reference
		return root.getInstrument(instrumentIndex).getOTF(OTFIndex).getObjectiveSettings().getRefractiveIndex();
		// Parent: u'Image'
		// Parent: u'OTF'
	}


	//
	// Path property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getPathDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getPathFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getPathFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getPathID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getPathLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getPathName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getPathStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getPathStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getPathStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getPathTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getPathTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getPathTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getPathTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Definition accessor
	public String getPathDefinition(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Path o = (Path) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDefinition();
		// Parent: u'Shape'
	}


	//
	// Pixels property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// AnnotationRef accessor
	public String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// DimensionOrder accessor
	public DimensionOrder getPixelsDimensionOrder(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// DimensionOrder is not a reference
		return root.getImage(imageIndex).getPixels().getDimensionOrder();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// ID accessor
	public String getPixelsID(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getID();
		// Parent: u'Image'
	}


	// Ignoring MetadataOnly element, complex property
	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// PhysicalSizeX accessor
	public Double getPixelsPhysicalSizeX(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeX is not a reference
		return root.getImage(imageIndex).getPixels().getPhysicalSizeX();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// PhysicalSizeY accessor
	public Double getPixelsPhysicalSizeY(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeY is not a reference
		return root.getImage(imageIndex).getPixels().getPhysicalSizeY();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// PhysicalSizeZ accessor
	public Double getPixelsPhysicalSizeZ(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeZ is not a reference
		return root.getImage(imageIndex).getPixels().getPhysicalSizeZ();
		// Parent: u'Image'
	}


	// Ignoring Plane element, complex property
	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// SizeC accessor
	public PositiveInteger getPixelsSizeC(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeC is not a reference
		return root.getImage(imageIndex).getPixels().getSizeC();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// SizeT accessor
	public PositiveInteger getPixelsSizeT(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeT is not a reference
		return root.getImage(imageIndex).getPixels().getSizeT();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// SizeX accessor
	public PositiveInteger getPixelsSizeX(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeX is not a reference
		return root.getImage(imageIndex).getPixels().getSizeX();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// SizeY accessor
	public PositiveInteger getPixelsSizeY(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeY is not a reference
		return root.getImage(imageIndex).getPixels().getSizeY();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// SizeZ accessor
	public PositiveInteger getPixelsSizeZ(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeZ is not a reference
		return root.getImage(imageIndex).getPixels().getSizeZ();
		// Parent: u'Image'
	}


	// Ignoring TiffData element, complex property
	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// TimeIncrement accessor
	public Double getPixelsTimeIncrement(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// TimeIncrement is not a reference
		return root.getImage(imageIndex).getPixels().getTimeIncrement();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// Type accessor
	public PixelType getPixelsType(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Type is not a reference
		return root.getImage(imageIndex).getPixels().getType();
		// Parent: u'Image'
	}


	//
	// Plane property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int planeIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// AnnotationRef accessor
	public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// DeltaT accessor
	public Double getPlaneDeltaT(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// DeltaT is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getDeltaT();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// ExposureTime accessor
	public Double getPlaneExposureTime(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ExposureTime is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getExposureTime();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// HashSHA1 accessor
	public String getPlaneHashSHA1(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// HashSHA1 is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getHashSHA1();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// PositionX accessor
	public Double getPlanePositionX(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionX is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getPositionX();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// PositionY accessor
	public Double getPlanePositionY(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionY is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getPositionY();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// PositionZ accessor
	public Double getPlanePositionZ(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionZ is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getPositionZ();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// TheC accessor
	public Integer getPlaneTheC(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheC is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getTheC();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// TheT accessor
	public Integer getPlaneTheT(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheT is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getTheT();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int planeIndex'])]
	// TheZ accessor
	public Integer getPlaneTheZ(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheZ is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getTheZ();
		// Parent: u'Pixels'
	}


	//
	// Plate property storage
	//
	// Indexes: {u'OME': [u'int plateIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// AnnotationRef accessor
	public String getPlateAnnotationRef(int plateIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// ColumnNamingConvention accessor
	public NamingConvention getPlateColumnNamingConvention(int plateIndex)
	{
		// Parents: {u'OME': None}
		// ColumnNamingConvention is not a reference
		return root.getPlate(plateIndex).getColumnNamingConvention();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// Columns accessor
	public Integer getPlateColumns(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Columns is not a reference
		return root.getPlate(plateIndex).getColumns();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// Description accessor
	public String getPlateDescription(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getPlate(plateIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// ExternalIdentifier accessor
	public String getPlateExternalIdentifier(int plateIndex)
	{
		// Parents: {u'OME': None}
		// ExternalIdentifier is not a reference
		return root.getPlate(plateIndex).getExternalIdentifier();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// ID accessor
	public String getPlateID(int plateIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getPlate(plateIndex).getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// Name accessor
	public String getPlateName(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getPlate(plateIndex).getName();
		// Parent: u'OME'
	}


	// Ignoring PlateAcquisition element, complex property
	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// RowNamingConvention accessor
	public NamingConvention getPlateRowNamingConvention(int plateIndex)
	{
		// Parents: {u'OME': None}
		// RowNamingConvention is not a reference
		return root.getPlate(plateIndex).getRowNamingConvention();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// Rows accessor
	public Integer getPlateRows(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Rows is not a reference
		return root.getPlate(plateIndex).getRows();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// ScreenRef accessor
	public String getPlateScreenRef(int plateIndex, int screenRefIndex)
	{
		// Parents: {u'OME': None}
		// ScreenRef is reference and occurs more than once
		return root.getPlate(plateIndex).getLinkedScreen(screenRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// Status accessor
	public String getPlateStatus(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Status is not a reference
		return root.getPlate(plateIndex).getStatus();
		// Parent: u'OME'
	}


	// Ignoring Well element, complex property
	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// WellOriginX accessor
	public Double getPlateWellOriginX(int plateIndex)
	{
		// Parents: {u'OME': None}
		// WellOriginX is not a reference
		return root.getPlate(plateIndex).getWellOriginX();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int plateIndex'])]
	// WellOriginY accessor
	public Double getPlateWellOriginY(int plateIndex)
	{
		// Parents: {u'OME': None}
		// WellOriginY is not a reference
		return root.getPlate(plateIndex).getWellOriginY();
		// Parent: u'OME'
	}


	//
	// PlateAcquisition property storage
	//
	// Indexes: {u'Plate': [u'int plateIndex', u'int plateAcquisitionIndex']}
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// AnnotationRef accessor
	public String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// Description accessor
	public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Description is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getDescription();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// EndTime accessor
	public String getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// EndTime is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getEndTime();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// ID accessor
	public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ID is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getID();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// MaximumFieldCount accessor
	public Integer getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// MaximumFieldCount is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getMaximumFieldCount();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// Name accessor
	public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Name is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getName();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// StartTime accessor
	public String getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// StartTime is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getStartTime();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int plateAcquisitionIndex'])]
	// WellSampleRef accessor
	public String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// WellSampleRef is reference and occurs more than once
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getLinkedWellSample(wellSampleRefIndex).getID();
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

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getPointDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getPointFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getPointFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getPointID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getPointLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getPointName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getPointStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getPointStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getPointStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getPointTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getPointTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getPointTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getPointTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X accessor
	public Double getPointX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y accessor
	public Double getPointY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
		// Parent: u'Shape'
	}


	//
	// Polyline property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getPolylineDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getPolylineFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getPolylineFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getPolylineID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getPolylineLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getPolylineName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getPolylineStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getPolylineTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getPolylineTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getPolylineTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getPolylineTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Closed accessor
	public Boolean getPolylineClosed(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getClosed();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Points accessor
	public String getPolylinePoints(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getPoints();
		// Parent: u'Shape'
	}


	//
	// Project property storage
	//
	// Indexes: {u'OME': [u'int projectIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int projectIndex'])]
	// AnnotationRef accessor
	public String getProjectAnnotationRef(int projectIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getProject(projectIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Ignoring Dataset_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int projectIndex'])]
	// Description accessor
	public String getProjectDescription(int projectIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getProject(projectIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int projectIndex'])]
	// ExperimenterRef accessor
	public String getProjectExperimenterRef(int projectIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getProject(projectIndex).getLinkedExperimenter().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int projectIndex'])]
	// GroupRef accessor
	public String getProjectGroupRef(int projectIndex)
	{
		// Parents: {u'OME': None}
		// GroupRef is reference and occurs only once
		return root.getProject(projectIndex).getLinkedGroup().getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int projectIndex'])]
	// ID accessor
	public String getProjectID(int projectIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getProject(projectIndex).getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int projectIndex'])]
	// Name accessor
	public String getProjectName(int projectIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getProject(projectIndex).getName();
		// Parent: u'OME'
	}


	//
	// ProjectRef property storage
	//
	// Indexes: {u'Dataset': [u'int datasetIndex', u'int projectRefIndex']}
	// {u'Dataset': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ProjectRef

	//
	// Pump property storage
	//
	// Indexes: {u'Laser': [u'int instrumentIndex', u'int lightSourceIndex']}
	// {u'Laser': {u'LightSource': {u'Instrument': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Pump

	//
	// ROI property storage
	//
	// Indexes: {u'OME': [u'int ROIIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int ROIIndex'])]
	// AnnotationRef accessor
	public String getROIAnnotationRef(int ROIIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getROI(ROIIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int ROIIndex'])]
	// Description accessor
	public String getROIDescription(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getROI(ROIIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int ROIIndex'])]
	// ID accessor
	public String getROIID(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getROI(ROIIndex).getID();
		// Parent: u'OME'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	// Indexes to iterate over: [(u'OME', [u'int ROIIndex'])]
	// Name accessor
	public String getROIName(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getROI(ROIIndex).getName();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int ROIIndex'])]
	// Namespace accessor
	public String getROINamespace(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Namespace is not a reference
		return root.getROI(ROIIndex).getNamespace();
		// Parent: u'OME'
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

	// Indexes to iterate over: [(u'Screen', [u'int screenIndex', u'int reagentIndex'])]
	// AnnotationRef accessor
	public String getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getScreen(screenIndex).getReagent(reagentIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Screen', [u'int screenIndex', u'int reagentIndex'])]
	// Description accessor
	public String getReagentDescription(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Description is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getDescription();
		// Parent: u'Screen'
	}


	// Indexes to iterate over: [(u'Screen', [u'int screenIndex', u'int reagentIndex'])]
	// ID accessor
	public String getReagentID(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// ID is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getID();
		// Parent: u'Screen'
	}


	// Indexes to iterate over: [(u'Screen', [u'int screenIndex', u'int reagentIndex'])]
	// Name accessor
	public String getReagentName(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Name is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getName();
		// Parent: u'Screen'
	}


	// Indexes to iterate over: [(u'Screen', [u'int screenIndex', u'int reagentIndex'])]
	// ReagentIdentifier accessor
	public String getReagentReagentIdentifier(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// ReagentIdentifier is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getReagentIdentifier();
		// Parent: u'Screen'
	}


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

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getRectangleDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getRectangleFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getRectangleFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getRectangleID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getRectangleLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getRectangleName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getRectangleStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getRectangleTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getRectangleTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getRectangleTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getRectangleTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Height accessor
	public Double getRectangleHeight(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getHeight();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Width accessor
	public Double getRectangleWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getWidth();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X accessor
	public Double getRectangleX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y accessor
	public Double getRectangleY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
		// Parent: u'Shape'
	}


	//
	// Screen property storage
	//
	// Indexes: {u'OME': [u'int screenIndex']}
	// {u'OME': None}
	// Is multi path? False

	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// AnnotationRef accessor
	public String getScreenAnnotationRef(int screenIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getScreen(screenIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// Description accessor
	public String getScreenDescription(int screenIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getScreen(screenIndex).getDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// ID accessor
	public String getScreenID(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getScreen(screenIndex).getID();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// Name accessor
	public String getScreenName(int screenIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getScreen(screenIndex).getName();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// PlateRef accessor
	public String getScreenPlateRef(int screenIndex, int plateRefIndex)
	{
		// Parents: {u'OME': None}
		// PlateRef is reference and occurs more than once
		return root.getScreen(screenIndex).getLinkedPlate(plateRefIndex).getID();
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// ProtocolDescription accessor
	public String getScreenProtocolDescription(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ProtocolDescription is not a reference
		return root.getScreen(screenIndex).getProtocolDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// ProtocolIdentifier accessor
	public String getScreenProtocolIdentifier(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ProtocolIdentifier is not a reference
		return root.getScreen(screenIndex).getProtocolIdentifier();
		// Parent: u'OME'
	}


	// Ignoring Reagent element, complex property
	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// ReagentSetDescription accessor
	public String getScreenReagentSetDescription(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ReagentSetDescription is not a reference
		return root.getScreen(screenIndex).getReagentSetDescription();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// ReagentSetIdentifier accessor
	public String getScreenReagentSetIdentifier(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ReagentSetIdentifier is not a reference
		return root.getScreen(screenIndex).getReagentSetIdentifier();
		// Parent: u'OME'
	}


	// Indexes to iterate over: [(u'OME', [u'int screenIndex'])]
	// Type accessor
	public String getScreenType(int screenIndex)
	{
		// Parents: {u'OME': None}
		// Type is not a reference
		return root.getScreen(screenIndex).getType();
		// Parent: u'OME'
	}


	//
	// ScreenRef property storage
	//
	// Indexes: {u'Plate': [u'int plateIndex', u'int screenRefIndex']}
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ScreenRef

	//
	// StageLabel property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// Name accessor
	public String getStageLabelName(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Name is not a reference
		return root.getImage(imageIndex).getStageLabel().getName();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// X accessor
	public Double getStageLabelX(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// X is not a reference
		return root.getImage(imageIndex).getStageLabel().getX();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// Y accessor
	public Double getStageLabelY(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Y is not a reference
		return root.getImage(imageIndex).getStageLabel().getY();
		// Parent: u'Image'
	}


	// Indexes to iterate over: [(u'Image', [u'int imageIndex'])]
	// Z accessor
	public Double getStageLabelZ(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Z is not a reference
		return root.getImage(imageIndex).getStageLabel().getZ();
		// Parent: u'Image'
	}


	//
	// StringAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int stringAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int stringAnnotationIndex'])]
	// ID accessor
	public String getStringAnnotationID(int stringAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getStringAnnotation(stringAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int stringAnnotationIndex'])]
	// Namespace accessor
	public String getStringAnnotationNamespace(int stringAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getStringAnnotation(stringAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int stringAnnotationIndex'])]
	// Value accessor
	public String getStringAnnotationValue(int stringAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getStringAnnotation(stringAnnotationIndex).getValue();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// StructuredAnnotations property storage
	//
	// Indexes: {u'OME': []}
	// {u'OME': None}
	// Is multi path? False

	// Ignoring BooleanAnnotation element, complex property
	// Ignoring DoubleAnnotation element, complex property
	// Ignoring FileAnnotation element, complex property
	// Ignoring ListAnnotation element, complex property
	// Ignoring LongAnnotation element, complex property
	// Ignoring StringAnnotation element, complex property
	// Ignoring TimestampAnnotation element, complex property
	// Ignoring XMLAnnotation element, complex property
	//
	// Text property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	// Description accessor from parent Shape
	public String getTextDescription(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getDescription();
		// Parent: u'Shape'
	}


	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getTextFill(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFill();
		// Parent: u'Shape'
	}


	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public Integer getTextFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
		// Parent: u'Shape'
	}


	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getTextID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
		// Parent: u'Shape'
	}


	// Label accessor from parent Shape
	public String getTextLabel(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLabel();
		// Parent: u'Shape'
	}


	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getTextName(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getName();
		// Parent: u'Shape'
	}


	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getTextStroke(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStroke();
		// Parent: u'Shape'
	}


	// StrokeDashArray accessor from parent Shape
	public String getTextStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
		// Parent: u'Shape'
	}


	// StrokeWidth accessor from parent Shape
	public Double getTextStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
		// Parent: u'Shape'
	}


	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public Integer getTextTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
		// Parent: u'Shape'
	}


	// TheT accessor from parent Shape
	public Integer getTextTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
		// Parent: u'Shape'
	}


	// TheZ accessor from parent Shape
	public Integer getTextTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
		// Parent: u'Shape'
	}


	// Transform accessor from parent Shape
	public String getTextTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Value accessor
	public String getTextValue(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getValue();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// X accessor
	public Double getTextX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
		// Parent: u'Shape'
	}


	// Indexes to iterate over: [(u'Shape', [u'int ROIIndex', u'int shapeIndex'])]
	// Y accessor
	public Double getTextY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary
		Text o = (Text) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
		// Parent: u'Shape'
	}


	//
	// TiffData property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int tiffDataIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int tiffDataIndex'])]
	// FirstC accessor
	public Integer getTiffDataFirstC(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstC is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getFirstC();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int tiffDataIndex'])]
	// FirstT accessor
	public Integer getTiffDataFirstT(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstT is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getFirstT();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int tiffDataIndex'])]
	// FirstZ accessor
	public Integer getTiffDataFirstZ(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstZ is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getFirstZ();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int tiffDataIndex'])]
	// IFD accessor
	public Integer getTiffDataIFD(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// IFD is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getIFD();
		// Parent: u'Pixels'
	}


	// Indexes to iterate over: [(u'Pixels', [u'int imageIndex', u'int tiffDataIndex'])]
	// PlaneCount accessor
	public Integer getTiffDataPlaneCount(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PlaneCount is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getPlaneCount();
		// Parent: u'Pixels'
	}


	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int timestampAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int timestampAnnotationIndex'])]
	// ID accessor
	public String getTimestampAnnotationID(int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int timestampAnnotationIndex'])]
	// Namespace accessor
	public String getTimestampAnnotationNamespace(int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int timestampAnnotationIndex'])]
	// Value accessor
	public String getTimestampAnnotationValue(int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getValue();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// TransmittanceRange property storage
	//
	// Indexes: {u'Filter': [u'int instrumentIndex', u'int filterIndex']}
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Filter', [u'int instrumentIndex', u'int filterIndex'])]
	// CutIn accessor
	public Integer getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutIn is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutIn();
		// Parent: u'Filter'
	}


	// Indexes to iterate over: [(u'Filter', [u'int instrumentIndex', u'int filterIndex'])]
	// CutInTolerance accessor
	public Integer getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutInTolerance is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutInTolerance();
		// Parent: u'Filter'
	}


	// Indexes to iterate over: [(u'Filter', [u'int instrumentIndex', u'int filterIndex'])]
	// CutOut accessor
	public Integer getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutOut is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutOut();
		// Parent: u'Filter'
	}


	// Indexes to iterate over: [(u'Filter', [u'int instrumentIndex', u'int filterIndex'])]
	// CutOutTolerance accessor
	public Integer getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutOutTolerance is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutOutTolerance();
		// Parent: u'Filter'
	}


	// Indexes to iterate over: [(u'Filter', [u'int instrumentIndex', u'int filterIndex'])]
	// Transmittance accessor
	public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// Transmittance is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getTransmittance();
		// Parent: u'Filter'
	}


	//
	// UUID property storage
	//
	// Indexes: {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'TiffData', [u'int imageIndex', u'int tiffDataIndex'])]
	// FileName accessor
	public String getUUIDFileName(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
		// FileName is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getUUID().getFileName();
		// Parent: u'TiffData'
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

	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// AnnotationRef accessor
	public String getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getWell(wellIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// Color accessor
	public Integer getWellColor(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Color is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getColor();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// Column accessor
	public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Column is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getColumn();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// ExternalDescription accessor
	public String getWellExternalDescription(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ExternalDescription is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getExternalDescription();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// ExternalIdentifier accessor
	public String getWellExternalIdentifier(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ExternalIdentifier is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getExternalIdentifier();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// ID accessor
	public String getWellID(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ID is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getID();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// ReagentRef accessor
	public String getWellReagentRef(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ReagentRef is reference and occurs only once
		return root.getPlate(plateIndex).getWell(wellIndex).getLinkedReagent().getID();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// Row accessor
	public NonNegativeInteger getWellRow(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Row is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getRow();
		// Parent: u'Plate'
	}


	// Indexes to iterate over: [(u'Plate', [u'int plateIndex', u'int wellIndex'])]
	// Status accessor
	public String getWellStatus(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Status is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getStatus();
		// Parent: u'Plate'
	}


	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// Indexes: {u'Well': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex']}
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// AnnotationRef accessor
	public String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}


	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// ID accessor
	public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// ID is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getID();
		// Parent: u'Well'
	}


	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// ImageRef accessor
	public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// ImageRef is reference and occurs only once
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getLinkedImage().getID();
		// Parent: u'Well'
	}


	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// Index accessor
	public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// Index is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getIndex();
		// Parent: u'Well'
	}


	// Ignoring PlateAcquisition_BackReference back reference
	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// PositionX accessor
	public Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// PositionX is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getPositionX();
		// Parent: u'Well'
	}


	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// PositionY accessor
	public Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// PositionY is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getPositionY();
		// Parent: u'Well'
	}


	// Indexes to iterate over: [(u'Well', [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex'])]
	// Timepoint accessor
	public Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// Timepoint is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getTimepoint();
		// Parent: u'Well'
	}


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

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int XMLAnnotationIndex'])]
	// ID accessor
	public String getXMLAnnotationID(int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getID();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int XMLAnnotationIndex'])]
	// Namespace accessor
	public String getXMLAnnotationNamespace(int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getNamespace();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	// Ignoring Shape_BackReference back reference
	// Indexes to iterate over: [(u'StructuredAnnotations', [u'int XMLAnnotationIndex'])]
	// Value accessor
	public String getXMLAnnotationValue(int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getValue();
		// Parent: u'StructuredAnnotations'
	}


	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference

	// - Entity storage -

	/** Sets the UUID associated with this collection of metadata. */
	public void setUUID(String uuid)
	{
		this.uuid = uuid;
	}

	// AnnotationRef property storage
	// {u'Plate': {u'OME': None}, u'ListAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'ROI': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Shape': {u'Union': {u'ROI': {u'OME': None}}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference AnnotationRef
	// Arc property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	public void setArcID(String id, int instrumentIndex, int lightSourceIndex)
{
}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	public void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
{
}

	public void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
{
}

	public void setArcModel(String model, int instrumentIndex, int lightSourceIndex)
{
}

	public void setArcPower(Double power, int instrumentIndex, int lightSourceIndex)
{
}

	public void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
{
}

	public void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex)
{
}

	// BinaryFile property storage
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	public void setFileAnnotationBinaryFileFileName(String fileName, int fileAnnotationIndex)
{
}

	public void setOTFBinaryFileFileName(String fileName, int instrumentIndex, int OTFIndex)
{
}

	public void setFileAnnotationBinaryFileMIMEType(String mimetype, int fileAnnotationIndex)
{
}

	public void setOTFBinaryFileMIMEType(String mimetype, int instrumentIndex, int OTFIndex)
{
}

	public void setFileAnnotationBinaryFileSize(Integer size, int fileAnnotationIndex)
{
}

	public void setOTFBinaryFileSize(Integer size, int instrumentIndex, int OTFIndex)
{
}

	// BooleanAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setBooleanAnnotationID(String id, int booleanAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex)
{
}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// Channel property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex)
{
}

	public void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex)
{
}

	public void setChannelColor(Integer color, int imageIndex, int channelIndex)
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
	public void setChannelNDFilter(Double ndfilter, int imageIndex, int channelIndex)
{
}

	public void setChannelName(String name, int imageIndex, int channelIndex)
{
}

	public void setChannelOTFRef(String otf, int imageIndex, int channelIndex)
{
}

	public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex)
{
}

	public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex)
{
}

	public void setChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int channelIndex)
{
}

	// Contact property storage
	// {u'Group': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference Contact
	// Dataset property storage
	// {u'OME': None}
	// Is multi path? False

	public void setDatasetAnnotationRef(String annotation, int datasetIndex, int annotationRefIndex)
{
}

	public void setDatasetDescription(String description, int datasetIndex)
{
}

	public void setDatasetExperimenterRef(String experimenter, int datasetIndex)
{
}

	public void setDatasetGroupRef(String group, int datasetIndex)
{
}

	public void setDatasetID(String id, int datasetIndex)
{
}

	// Ignoring Image_BackReference back reference
	public void setDatasetName(String name, int datasetIndex)
{
}

	public void setDatasetProjectRef(String project, int datasetIndex, int projectRefIndex)
{
}

	// DatasetRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference DatasetRef
	// Detector property storage
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

	// DetectorSettings property storage
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

	public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex)
{
}

	public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex)
{
}

	public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex)
{
}

	// Dichroic property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public void setDichroicID(String id, int instrumentIndex, int dichroicIndex)
{
}

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

	// DichroicRef property storage
	// {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference DichroicRef
	// DoubleAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setDoubleAnnotationID(String id, int doubleAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex)
{
}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// Ellipse property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	public void setEllipseDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setEllipseFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setEllipseFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setEllipseID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setEllipseLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setEllipseName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setEllipseStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setEllipseStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setEllipseTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setEllipseTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setEllipseTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setEllipseTransform(String transform, int ROIIndex, int shapeIndex)
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

	// ExperimentRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ExperimentRef
	// Experimenter property storage
	// {u'OME': None}
	// Is multi path? False

	public void setExperimenterAnnotationRef(String annotation, int experimenterIndex, int annotationRefIndex)
{
}

	// Ignoring Dataset_BackReference back reference
	public void setExperimenterDisplayName(String displayName, int experimenterIndex)
{
}

	public void setExperimenterEmail(String email, int experimenterIndex)
{
}

	// Ignoring Experiment_BackReference back reference
	public void setExperimenterFirstName(String firstName, int experimenterIndex)
{
}

	public void setExperimenterGroupRef(String group, int experimenterIndex, int groupRefIndex)
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

	// ExperimenterRef property storage
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ExperimenterRef
	// Filament property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	public void setFilamentID(String id, int instrumentIndex, int lightSourceIndex)
{
}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
{
}

	public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
{
}

	public void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex)
{
}

	public void setFilamentPower(Double power, int instrumentIndex, int lightSourceIndex)
{
}

	public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
{
}

	public void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex)
{
}

	// FileAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setFileAnnotationID(String id, int fileAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// Filter property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
{
}

	public void setFilterID(String id, int instrumentIndex, int filterIndex)
{
}

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

	// FilterSet property storage
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

	public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
{
}

	public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
{
}

	public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
{
}

	// Ignoring OTF_BackReference back reference
	public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex)
{
}

	// FilterSetRef property storage
	// {u'OTF': {u'Instrument': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference FilterSetRef
	// Group property storage
	// {u'OME': None}
	// Is multi path? False

	public void setGroupContact(String contact, int groupIndex)
{
}

	// Ignoring Dataset_BackReference back reference
	public void setGroupDescription(String description, int groupIndex)
{
}

	// Ignoring Experimenter_BackReference back reference
	public void setGroupID(String id, int groupIndex)
{
}

	// Ignoring Image_BackReference back reference
	public void setGroupLeader(String leader, int groupIndex)
{
}

	public void setGroupName(String name, int groupIndex)
{
}

	// Ignoring Project_BackReference back reference
	// GroupRef property storage
	// {u'Project': {u'OME': None}, u'Image': {u'OME': None}, u'Experimenter': {u'OME': None}, u'Dataset': {u'OME': None}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference GroupRef
	// Image property storage
	// {u'OME': None}
	// Is multi path? False

	public void setImageAcquiredDate(String acquiredDate, int imageIndex)
{
}

	public void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
{
}

	public void setImageDatasetRef(String dataset, int imageIndex, int datasetRefIndex)
{
}

	public void setImageDescription(String description, int imageIndex)
{
}

	public void setImageExperimentRef(String experiment, int imageIndex)
{
}

	public void setImageExperimenterRef(String experimenter, int imageIndex)
{
}

	public void setImageGroupRef(String group, int imageIndex)
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
	// ImageRef property storage
	// {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ImageRef
	// ImagingEnvironment property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex)
{
}

	public void setImagingEnvironmentCO2Percent(PercentFraction co2percent, int imageIndex)
{
}

	public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex)
{
}

	public void setImagingEnvironmentTemperature(Double temperature, int imageIndex)
{
}

	// Instrument property storage
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
	// Ignoring OTF element, complex property
	// Ignoring Objective element, complex property
	// InstrumentRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference InstrumentRef
	// Laser property storage
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// Ignoring Arc of parent abstract type
	// Ignoring Filament of parent abstract type
	public void setLaserID(String id, int instrumentIndex, int lightSourceIndex)
{
}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	public void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLaserModel(String model, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLaserPower(Double power, int instrumentIndex, int lightSourceIndex)
{
}

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
	public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex)
{
}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightSourceIndex)
{
}

	public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
{
}

	// LightPath property storage
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

	// LightSourceSettings property storage
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

	public void setChannelLightSourceSettingsWavelength(PositiveInteger wavelength, int imageIndex, int channelIndex)
{
}

	public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
{
}

	// Line property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	public void setLineDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setLineFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setLineFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setLineID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setLineLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setLineName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setLineStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setLineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setLineTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setLineTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setLineTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setLineTransform(String transform, int ROIIndex, int shapeIndex)
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

	// ListAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setListAnnotationAnnotationRef(String annotation, int listAnnotationIndex, int annotationRefIndex)
{
}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setListAnnotationID(String id, int listAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// LongAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setLongAnnotationID(String id, int longAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	public void setLongAnnotationValue(Long value, int longAnnotationIndex)
{
}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// Mask property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	public void setMaskDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setMaskFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setMaskFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setMaskID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setMaskLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setMaskName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setMaskStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setMaskStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setMaskTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setMaskTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setMaskTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setMaskTransform(String transform, int ROIIndex, int shapeIndex)
{
}

	// Ignoring BinData element, complex property
	public void setMaskX(Double x, int ROIIndex, int shapeIndex)
{
}

	public void setMaskY(Double y, int ROIIndex, int shapeIndex)
{
}

	// MetadataOnly property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	// MicrobeamManipulation property storage
	// {u'Experiment': {u'OME': None}}
	// Is multi path? False

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

	// MicrobeamManipulationRef property storage
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference MicrobeamManipulationRef
	// Microscope property storage
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

	// OTF property storage
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	public void setOTFFilterSetRef(String filterSet, int instrumentIndex, int OTFIndex)
{
}

	public void setOTFID(String id, int instrumentIndex, int OTFIndex)
{
}

	// Ignoring ObjectiveSettings element, complex property
	public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int OTFIndex)
{
}

	public void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int OTFIndex)
{
}

	public void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int OTFIndex)
{
}

	public void setOTFType(PixelType type, int instrumentIndex, int OTFIndex)
{
}

	// OTFRef property storage
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference OTFRef
	// Objective property storage
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

	public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex)
{
}

	public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
{
}

	public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex)
{
}

	// ObjectiveSettings property storage
	// {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	public void setImageObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex)
{
}

	public void setOTFObjectiveSettingsCorrectionCollar(Double correctionCollar, int instrumentIndex, int OTFIndex)
{
}

	public void setImageObjectiveSettingsID(String id, int imageIndex)
{
}

	public void setOTFObjectiveSettingsID(String id, int instrumentIndex, int OTFIndex)
{
}

	public void setImageObjectiveSettingsMedium(Medium medium, int imageIndex)
{
}

	public void setOTFObjectiveSettingsMedium(Medium medium, int instrumentIndex, int OTFIndex)
{
}

	public void setImageObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex)
{
}

	public void setOTFObjectiveSettingsRefractiveIndex(Double refractiveIndex, int instrumentIndex, int OTFIndex)
{
}

	// Path property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	public void setPathDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setPathFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setPathFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setPathID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setPathLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setPathName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setPathStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setPathStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setPathStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setPathTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setPathTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setPathTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setPathTransform(String transform, int ROIIndex, int shapeIndex)
{
}

	public void setPathDefinition(String definition, int ROIIndex, int shapeIndex)
{
}

	// Pixels property storage
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
	public void setPixelsPhysicalSizeX(Double physicalSizeX, int imageIndex)
{
}

	public void setPixelsPhysicalSizeY(Double physicalSizeY, int imageIndex)
{
}

	public void setPixelsPhysicalSizeZ(Double physicalSizeZ, int imageIndex)
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

	// Plane property storage
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

	public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex)
{
}

	public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex)
{
}

	public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex)
{
}

	public void setPlaneTheC(Integer theC, int imageIndex, int planeIndex)
{
}

	public void setPlaneTheT(Integer theT, int imageIndex, int planeIndex)
{
}

	public void setPlaneTheZ(Integer theZ, int imageIndex, int planeIndex)
{
}

	// Plate property storage
	// {u'OME': None}
	// Is multi path? False

	public void setPlateAnnotationRef(String annotation, int plateIndex, int annotationRefIndex)
{
}

	public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex)
{
}

	public void setPlateColumns(Integer columns, int plateIndex)
{
}

	public void setPlateDescription(String description, int plateIndex)
{
}

	public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
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

	public void setPlateRows(Integer rows, int plateIndex)
{
}

	public void setPlateScreenRef(String screen, int plateIndex, int screenRefIndex)
{
}

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

	// PlateAcquisition property storage
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public void setPlateAcquisitionAnnotationRef(String annotation, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
{
}

	public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex)
{
}

	public void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex)
{
}

	public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex)
{
}

	public void setPlateAcquisitionMaximumFieldCount(Integer maximumFieldCount, int plateIndex, int plateAcquisitionIndex)
{
}

	public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex)
{
}

	public void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex)
{
}

	public void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
{
}

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
	public void setPointDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setPointFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setPointFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setPointID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setPointLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setPointName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setPointStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setPointStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setPointTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setPointTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setPointTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setPointTransform(String transform, int ROIIndex, int shapeIndex)
{
}

	public void setPointX(Double x, int ROIIndex, int shapeIndex)
{
}

	public void setPointY(Double y, int ROIIndex, int shapeIndex)
{
}

	// Polyline property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	public void setPolylineDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setPolylineFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setPolylineFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setPolylineID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setPolylineName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setPolylineStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setPolylineTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineTransform(String transform, int ROIIndex, int shapeIndex)
{
}

	public void setPolylineClosed(Boolean closed, int ROIIndex, int shapeIndex)
{
}

	public void setPolylinePoints(String points, int ROIIndex, int shapeIndex)
{
}

	// Project property storage
	// {u'OME': None}
	// Is multi path? False

	public void setProjectAnnotationRef(String annotation, int projectIndex, int annotationRefIndex)
{
}

	// Ignoring Dataset_BackReference back reference
	public void setProjectDescription(String description, int projectIndex)
{
}

	public void setProjectExperimenterRef(String experimenter, int projectIndex)
{
}

	public void setProjectGroupRef(String group, int projectIndex)
{
}

	public void setProjectID(String id, int projectIndex)
{
}

	public void setProjectName(String name, int projectIndex)
{
}

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
	// ROIRef property storage
	// {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	// 1:1
	// Is multi path? True
	// Ignoring ID property of reference ROIRef
	// Reagent property storage
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

	// Ignoring Well_BackReference back reference
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
	public void setRectangleDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setRectangleFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setRectangleFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setRectangleID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setRectangleLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setRectangleName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setRectangleStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setRectangleStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setRectangleTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setRectangleTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setRectangleTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setRectangleTransform(String transform, int ROIIndex, int shapeIndex)
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

	// Screen property storage
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

	// ScreenRef property storage
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference ScreenRef
	// StageLabel property storage
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

	// StringAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setStringAnnotationID(String id, int stringAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
	public void setStringAnnotationNamespace(String namespace, int stringAnnotationIndex)
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
	// Ignoring Shape_BackReference back reference
	public void setStringAnnotationValue(String value, int stringAnnotationIndex)
{
}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// StructuredAnnotations property storage
	// {u'OME': None}
	// Is multi path? False

	// Ignoring BooleanAnnotation element, complex property
	// Ignoring DoubleAnnotation element, complex property
	// Ignoring FileAnnotation element, complex property
	// Ignoring ListAnnotation element, complex property
	// Ignoring LongAnnotation element, complex property
	// Ignoring StringAnnotation element, complex property
	// Ignoring TimestampAnnotation element, complex property
	// Ignoring XMLAnnotation element, complex property
	// Text property storage
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Ignoring AnnotationRef of parent abstract type
	public void setTextDescription(String description, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Ellipse of parent abstract type
	public void setTextFill(Integer fill, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	public void setTextFontSize(Integer fontSize, int ROIIndex, int shapeIndex)
{
}

	// Ignoring FontStyle of parent abstract type
	public void setTextID(String id, int ROIIndex, int shapeIndex)
{
}

	public void setTextLabel(String label, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	public void setTextName(String name, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	public void setTextStroke(Integer stroke, int ROIIndex, int shapeIndex)
{
}

	public void setTextStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
{
}

	public void setTextStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
{
}

	// Ignoring Text of parent abstract type
	public void setTextTheC(Integer theC, int ROIIndex, int shapeIndex)
{
}

	public void setTextTheT(Integer theT, int ROIIndex, int shapeIndex)
{
}

	public void setTextTheZ(Integer theZ, int ROIIndex, int shapeIndex)
{
}

	public void setTextTransform(String transform, int ROIIndex, int shapeIndex)
{
}

	public void setTextValue(String value, int ROIIndex, int shapeIndex)
{
}

	public void setTextX(Double x, int ROIIndex, int shapeIndex)
{
}

	public void setTextY(Double y, int ROIIndex, int shapeIndex)
{
}

	// TiffData property storage
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setTiffDataFirstC(Integer firstC, int imageIndex, int tiffDataIndex)
{
}

	public void setTiffDataFirstT(Integer firstT, int imageIndex, int tiffDataIndex)
{
}

	public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int tiffDataIndex)
{
}

	public void setTiffDataIFD(Integer ifd, int imageIndex, int tiffDataIndex)
{
}

	public void setTiffDataPlaneCount(Integer planeCount, int imageIndex, int tiffDataIndex)
{
}

	// Ignoring UUID element, complex property
	// TimestampAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTimestampAnnotationID(String id, int timestampAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	public void setTimestampAnnotationValue(String value, int timestampAnnotationIndex)
{
}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	// TransmittanceRange property storage
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	public void setTransmittanceRangeCutIn(Integer cutIn, int instrumentIndex, int filterIndex)
{
}

	public void setTransmittanceRangeCutInTolerance(Integer cutInTolerance, int instrumentIndex, int filterIndex)
{
}

	public void setTransmittanceRangeCutOut(Integer cutOut, int instrumentIndex, int filterIndex)
{
}

	public void setTransmittanceRangeCutOutTolerance(Integer cutOutTolerance, int instrumentIndex, int filterIndex)
{
}

	public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex)
{
}

	// UUID property storage
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex)
{
}

	// Union property storage
	// {u'ROI': {u'OME': None}}
	// Is multi path? False

	// Ignoring Shape element, complex property
	// Well property storage
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public void setWellAnnotationRef(String annotation, int plateIndex, int wellIndex, int annotationRefIndex)
{
}

	public void setWellColor(Integer color, int plateIndex, int wellIndex)
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

	public void setWellStatus(String status, int plateIndex, int wellIndex)
{
}

	// Ignoring WellSample element, complex property
	// WellSample property storage
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

	public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
{
}

	// WellSampleRef property storage
	// {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	// 1:1
	// Is multi path? False
	// Ignoring ID property of reference WellSampleRef
	// XMLAnnotation property storage
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setXMLAnnotationID(String id, int XMLAnnotationIndex)
{
}

	// Ignoring Image_BackReference back reference
	// Ignoring ListAnnotation_BackReference back reference
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
	// Ignoring Shape_BackReference back reference
	public void setXMLAnnotationValue(String value, int XMLAnnotationIndex)
{
}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
