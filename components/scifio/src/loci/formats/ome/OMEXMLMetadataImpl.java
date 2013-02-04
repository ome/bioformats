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
 * Created by rleigh via xsd-fu on 2013-02-04 15:52:37.949143
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.ome;

import ome.xml.model.*;
import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

/**
 * A metadata store implementation for constructing and manipulating OME-XML
 * DOMs for the current version of OME-XML. It requires the
 * ome.xml.model package to compile (part of ome-xml.jar).
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/ome/OMEXMLMetadataImpl.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/ome/OMEXMLMetadataImpl.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 * @author Melissa Linkert linkert at wisc.edu
 */
public class OMEXMLMetadataImpl extends AbstractOMEXMLMetadata
{
	private OME root;

	private OMEModel model;

	public OMEXMLMetadataImpl()
	{
		createRoot();
	}

	public void createRoot()
	{
		root = new OME();
		model = new OMEModelImpl();
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
		model = new OMEModelImpl();
	}

	public String dumpXML()
	{
		resolveReferences();
		return super.dumpXML();
	}

	public int resolveReferences()
	{
		return model.resolveReferences();
	}

	// -- Entity counting (manual definitions) --

	public int getPixelsBinDataCount(int imageIndex)
	{
		return root.getImage(imageIndex).getPixels().sizeOfBinDataList();
	}

  public int getBooleanAnnotationAnnotationCount(int booleanAnnotationIndex) {
    return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getCommentAnnotationAnnotationCount(int commentAnnotationIndex) {
    return root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getDoubleAnnotationAnnotationCount(int doubleAnnotationIndex) {
    return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getFileAnnotationAnnotationCount(int fileAnnotationIndex) {
    return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getListAnnotationAnnotationCount(int listAnnotationIndex) {
    return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getLongAnnotationAnnotationCount(int longAnnotationIndex) {
    return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getTagAnnotationAnnotationCount(int tagAnnotationIndex) {
    return root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getTermAnnotationAnnotationCount(int termAnnotationIndex) {
    return root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getTimestampAnnotationAnnotationCount(int timestampAnnotationIndex)
  {
    return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).sizeOfLinkedAnnotationList();
  }

  public int getXMLAnnotationAnnotationCount(int xmlAnnotationIndex) {
    return root.getStructuredAnnotations().getXMLAnnotation(xmlAnnotationIndex).sizeOfLinkedAnnotationList();
  }

	public String getLightSourceType(int instrumentIndex, int lightSourceIndex)
	{
		LightSource o = root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		String className = o.getClass().getName();
		return className.substring(
			className.lastIndexOf('.') + 1, className.length());
	}

  public String getShapeType(int roiIndex, int shapeIndex)
  {
    Shape o = root.getROI(roiIndex).getUnion().getShape(shapeIndex);
    String className = o.getClass().getName();
    return className.substring(
      className.lastIndexOf('.') + 1, className.length());
  }

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	public int getROIAnnotationRefCount(int ROIIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getROI(ROIIndex).sizeOfLinkedAnnotationList();
	}

	public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).sizeOfLinkedAnnotationList();
	}

	public int getPlateAnnotationRefCount(int plateIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getPlate(plateIndex).sizeOfLinkedAnnotationList();
	}

	public int getExperimenterGroupAnnotationRefCount(int experimenterGroupIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getExperimenterGroup(experimenterGroupIndex).sizeOfLinkedAnnotationList();
	}

	public int getImageAnnotationRefCount(int imageIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getImage(imageIndex).sizeOfLinkedAnnotationList();
	}

	public int getScreenAnnotationRefCount(int screenIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getScreen(screenIndex).sizeOfLinkedAnnotationList();
	}

	public int getWellAnnotationRefCount(int plateIndex, int wellIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getPlate(plateIndex).getWell(wellIndex).sizeOfLinkedAnnotationList();
	}

	public int getDatasetAnnotationRefCount(int datasetIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getDataset(datasetIndex).sizeOfLinkedAnnotationList();
	}

	public int getProjectAnnotationRefCount(int projectIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getProject(projectIndex).sizeOfLinkedAnnotationList();
	}

	public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).sizeOfLinkedAnnotationList();
	}

	public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).sizeOfLinkedAnnotationList();
	}

	public int getExperimenterAnnotationRefCount(int experimenterIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getExperimenter(experimenterIndex).sizeOfLinkedAnnotationList();
	}

	public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).sizeOfLinkedAnnotationList();
	}

	public int getPixelsAnnotationRefCount(int imageIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getImage(imageIndex).getPixels().sizeOfLinkedAnnotationList();
	}

	public int getChannelAnnotationRefCount(int imageIndex, int channelIndex)
	{
		// Parents: {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// AnnotationRef is a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).sizeOfLinkedAnnotationList();
	}

	// Arc entity counting
	// BinaryFile entity counting
	// BinaryOnly entity counting
	// BooleanAnnotation entity counting
	public int getBooleanAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// BooleanAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfBooleanAnnotationList();
	}

	// Channel entity counting
	public int getChannelCount(int imageIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Channel is not a reference
		return root.getImage(imageIndex).getPixels().sizeOfChannelList();
	}

	// CommentAnnotation entity counting
	public int getCommentAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// CommentAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfCommentAnnotationList();
	}

	// Dataset entity counting
	public int getDatasetCount()
	{
		// Parents: {u'OME': None}
		// Dataset is not a reference
		return root.sizeOfDatasetList();
	}

	// DatasetRef entity counting
	public int getDatasetRefCount(int projectIndex)
	{
		// Parents: {u'Project': {u'OME': None}}
		// DatasetRef is a reference
		return root.getProject(projectIndex).sizeOfLinkedDatasetList();
	}

	// Detector entity counting
	public int getDetectorCount(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Detector is not a reference
		return root.getInstrument(instrumentIndex).sizeOfDetectorList();
	}

	// DetectorSettings entity counting
	// Dichroic entity counting
	public int getDichroicCount(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Dichroic is not a reference
		return root.getInstrument(instrumentIndex).sizeOfDichroicList();
	}

	// DichroicRef entity counting
	// DoubleAnnotation entity counting
	public int getDoubleAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// DoubleAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfDoubleAnnotationList();
	}

	// Ellipse entity counting
	// EmissionFilterRef entity counting
	public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex)
	{
		// Parents: {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
		// EmissionFilterRef is a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().sizeOfLinkedEmissionFilterList();
	}

	public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
		// EmissionFilterRef is a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).sizeOfLinkedEmissionFilterList();
	}

	// ExcitationFilterRef entity counting
	public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex)
	{
		// Parents: {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
		// ExcitationFilterRef is a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().sizeOfLinkedExcitationFilterList();
	}

	public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'LightPath': {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}, u'FilterSet': {u'Instrument': {u'OME': None}}}
		// ExcitationFilterRef is a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).sizeOfLinkedExcitationFilterList();
	}

	// Experiment entity counting
	public int getExperimentCount()
	{
		// Parents: {u'OME': None}
		// Experiment is not a reference
		return root.sizeOfExperimentList();
	}

	// ExperimentRef entity counting
	// Experimenter entity counting
	public int getExperimenterCount()
	{
		// Parents: {u'OME': None}
		// Experimenter is not a reference
		return root.sizeOfExperimenterList();
	}

	// ExperimenterGroup entity counting
	public int getExperimenterGroupCount()
	{
		// Parents: {u'OME': None}
		// ExperimenterGroup is not a reference
		return root.sizeOfExperimenterGroupList();
	}

	// ExperimenterGroupRef entity counting
	// ExperimenterRef entity counting
	public int getExperimenterGroupExperimenterRefCount(int experimenterGroupIndex)
	{
		// Parents: {u'ExperimenterGroup': {u'OME': None}, u'Image': {u'OME': None}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Experiment': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ExperimenterRef is a reference
		return root.getExperimenterGroup(experimenterGroupIndex).sizeOfLinkedExperimenterList();
	}

	// Filament entity counting
	// FileAnnotation entity counting
	public int getFileAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// FileAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfFileAnnotationList();
	}

	// Filter entity counting
	public int getFilterCount(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Filter is not a reference
		return root.getInstrument(instrumentIndex).sizeOfFilterList();
	}

	// FilterSet entity counting
	public int getFilterSetCount(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// FilterSet is not a reference
		return root.getInstrument(instrumentIndex).sizeOfFilterSetList();
	}

	// FilterSetRef entity counting
	// Image entity counting
	public int getImageCount()
	{
		// Parents: {u'OME': None}
		// Image is not a reference
		return root.sizeOfImageList();
	}

	// ImageRef entity counting
	public int getDatasetImageRefCount(int datasetIndex)
	{
		// Parents: {u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Dataset': {u'OME': None}}
		// ImageRef is a reference
		return root.getDataset(datasetIndex).sizeOfLinkedImageList();
	}

	// ImagingEnvironment entity counting
	// Instrument entity counting
	public int getInstrumentCount()
	{
		// Parents: {u'OME': None}
		// Instrument is not a reference
		return root.sizeOfInstrumentList();
	}

	// InstrumentRef entity counting
	// Label entity counting
	// Laser entity counting
	// Leader entity counting
	public int getLeaderCount(int experimenterGroupIndex)
	{
		// Parents: {u'ExperimenterGroup': {u'OME': None}}
		// Leader is a reference
		return root.getExperimenterGroup(experimenterGroupIndex).sizeOfLinkedLeaderList();
	}

	// LightEmittingDiode entity counting
	// LightPath entity counting
	// LightSource entity counting
	public int getLightSourceCount(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LightSource is not a reference
		return root.getInstrument(instrumentIndex).sizeOfLightSourceList();
	}

	// LightSourceSettings entity counting
	public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// LightSourceSettings is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).sizeOfLightSourceSettingsList();
	}

	// Line entity counting
	// ListAnnotation entity counting
	public int getListAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ListAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfListAnnotationList();
	}

	// LongAnnotation entity counting
	public int getLongAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// LongAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfLongAnnotationList();
	}

	// Mask entity counting
	// MetadataOnly entity counting
	// MicrobeamManipulation entity counting
	public int getMicrobeamManipulationCount(int experimentIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// MicrobeamManipulation is not a reference
		return root.getExperiment(experimentIndex).sizeOfMicrobeamManipulationList();
	}

	// MicrobeamManipulationRef entity counting
	public int getMicrobeamManipulationRefCount(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// MicrobeamManipulationRef is a reference
		return root.getImage(imageIndex).sizeOfLinkedMicrobeamManipulationList();
	}

	// Microscope entity counting
	// Objective entity counting
	public int getObjectiveCount(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Objective is not a reference
		return root.getInstrument(instrumentIndex).sizeOfObjectiveList();
	}

	// ObjectiveSettings entity counting
	// Pixels entity counting
	// Plane entity counting
	public int getPlaneCount(int imageIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Plane is not a reference
		return root.getImage(imageIndex).getPixels().sizeOfPlaneList();
	}

	// Plate entity counting
	public int getPlateCount()
	{
		// Parents: {u'OME': None}
		// Plate is not a reference
		return root.sizeOfPlateList();
	}

	// PlateAcquisition entity counting
	public int getPlateAcquisitionCount(int plateIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// PlateAcquisition is not a reference
		return root.getPlate(plateIndex).sizeOfPlateAcquisitionList();
	}

	// PlateRef entity counting
	public int getPlateRefCount(int screenIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// PlateRef is a reference
		return root.getScreen(screenIndex).sizeOfLinkedPlateList();
	}

	// Point entity counting
	// Polygon entity counting
	// Polyline entity counting
	// Project entity counting
	public int getProjectCount()
	{
		// Parents: {u'OME': None}
		// Project is not a reference
		return root.sizeOfProjectList();
	}

	// Pump entity counting
	// ROI entity counting
	public int getROICount()
	{
		// Parents: {u'OME': None}
		// ROI is not a reference
		return root.sizeOfROIList();
	}

	// ROIRef entity counting
	public int getImageROIRefCount(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ROIRef is a reference
		return root.getImage(imageIndex).sizeOfLinkedROIList();
	}

	public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Image': {u'OME': None}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ROIRef is a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).sizeOfLinkedROIList();
	}

	// Reagent entity counting
	public int getReagentCount(int screenIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Reagent is not a reference
		return root.getScreen(screenIndex).sizeOfReagentList();
	}

	// ReagentRef entity counting
	// Rectangle entity counting
	// Screen entity counting
	public int getScreenCount()
	{
		// Parents: {u'OME': None}
		// Screen is not a reference
		return root.sizeOfScreenList();
	}

	// Shape entity counting
	public int getShapeCount(int ROIIndex)
	{
		// Parents: {u'Union': {u'ROI': {u'OME': None}}}
		// Shape is not a reference
		return root.getROI(ROIIndex).getUnion().sizeOfShapeList();
	}

	// StageLabel entity counting
	// StructuredAnnotations entity counting
	// TagAnnotation entity counting
	public int getTagAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// TagAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfTagAnnotationList();
	}

	// TermAnnotation entity counting
	public int getTermAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// TermAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfTermAnnotationList();
	}

	// TiffData entity counting
	public int getTiffDataCount(int imageIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TiffData is not a reference
		return root.getImage(imageIndex).getPixels().sizeOfTiffDataList();
	}

	// TimestampAnnotation entity counting
	public int getTimestampAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// TimestampAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfTimestampAnnotationList();
	}

	// TransmittanceRange entity counting
	// Element's text data
	// {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	public void setUUIDValue(String value, int imageIndex, int tiffDataIndex)
	{
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		if (o3.getUUID() == null)
		{
			o3.setUUID(new UUID());
		}
		UUID o4 = o3.getUUID();
		o4.setValue(value);
	}

	public String getUUIDValue(int imageIndex, int tiffDataIndex)
	{
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getUUID().getValue();
	}

	// UUID entity counting
	// Union entity counting
	// Well entity counting
	public int getWellCount(int plateIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Well is not a reference
		return root.getPlate(plateIndex).sizeOfWellList();
	}

	// WellSample entity counting
	public int getWellSampleCount(int plateIndex, int wellIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// WellSample is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).sizeOfWellSampleList();
	}

	// WellSampleRef entity counting
	public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'PlateAcquisition': {u'Plate': {u'OME': None}}}
		// WellSampleRef is a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).sizeOfLinkedWellSampleList();
	}

	// XMLAnnotation entity counting
	public int getXMLAnnotationCount()
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// XMLAnnotation is not a reference
		return root.getStructuredAnnotations().sizeOfXMLAnnotationList();
	}


	// -- Entity retrieval (manual definitions) --

	public Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex)
	{
		return root.getImage(imageIndex).getPixels().getBinData(binDataIndex).getBigEndian();
	}

	// -- Entity retrieval (code generated definitions) --

	/** Gets the UUID associated with this collection of metadata. */
	public String getUUID()
	{
		return root.getUUID();
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getArcLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
	}

	// Manufacturer accessor from parent LightSource
	public String getArcManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
	}

	// Model accessor from parent LightSource
	public String getArcModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
	}

	// Power accessor from parent LightSource
	public Double getArcPower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
	}

	// SerialNumber accessor from parent LightSource
	public String getArcSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
	}

	public ArcType getArcType(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Arc o = (Arc) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getType();
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
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
		// FileName is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getBinaryFile().getFileName();
	}

	public String getBinaryFileMIMEType(int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
		// MIMEType is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getBinaryFile().getMIMEType();
	}

	public NonNegativeLong getBinaryFileSize(int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
		// Size is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getBinaryFile().getSize();
	}

	//
	// BinaryOnly property storage
	//
	// Indexes: {u'OME': []}
	// {u'OME': None}
	// Is multi path? False

	public String getBinaryOnlyMetadataFile(int metadataFileIndex)
	{
		// Parents: {u'OME': None}
		// MetadataFile is not a reference
		return root.getBinaryOnly().getMetadataFile();
	}

	public String getBinaryOnlyUUID(int UUIDIndex)
	{
		// Parents: {u'OME': None}
		// UUID is not a reference
		return root.getBinaryOnly().getUUID();
	}

	//
	// BooleanAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int booleanAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getBooleanAnnotationAnnotationRef(int booleanAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getBooleanAnnotationDescription(int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getBooleanAnnotationID(int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getBooleanAnnotationNamespace(int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex).getValue();
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
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AcquisitionMode is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getAcquisitionMode();
	}

	public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public Color getChannelColor(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Color is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getColor();
	}

	public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ContrastMethod is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getContrastMethod();
	}

	// Ignoring DetectorSettings element, complex property
	public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// EmissionWavelength is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getEmissionWavelength();
	}

	public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ExcitationWavelength is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getExcitationWavelength();
	}

	public String getChannelFilterSetRef(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FilterSetRef is reference and occurs only once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLinkedFilterSet().getID();
	}

	public String getChannelFluor(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Fluor is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getFluor();
	}

	public String getChannelID(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getID();
	}

	public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// IlluminationType is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getIlluminationType();
	}

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	public Double getChannelNDFilter(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// NDFilter is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getNDFilter();
	}

	public String getChannelName(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Name is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getName();
	}

	public Double getChannelPinholeSize(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PinholeSize is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getPinholeSize();
	}

	// Ignoring Pixels_BackReference back reference
	public Integer getChannelPockelCellSetting(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PockelCellSetting is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getPockelCellSetting();
	}

	public PositiveInteger getChannelSamplesPerPixel(int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// SamplesPerPixel is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getSamplesPerPixel();
	}

	//
	// CommentAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int commentAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getCommentAnnotationAnnotationRef(int commentAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getCommentAnnotationDescription(int commentAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getCommentAnnotationID(int commentAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getCommentAnnotationNamespace(int commentAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex).getValue();
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
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getDataset(datasetIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getDatasetDescription(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getDataset(datasetIndex).getDescription();
	}

	public String getDatasetExperimenterGroupRef(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterGroupRef is reference and occurs only once
		return root.getDataset(datasetIndex).getLinkedExperimenterGroup().getID();
	}

	public String getDatasetExperimenterRef(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getDataset(datasetIndex).getLinkedExperimenter().getID();
	}

	public String getDatasetID(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getDataset(datasetIndex).getID();
	}

	public String getDatasetImageRef(int datasetIndex, int imageRefIndex)
	{
		// Parents: {u'OME': None}
		// ImageRef is reference and occurs more than once
		return root.getDataset(datasetIndex).getLinkedImage(imageRefIndex).getID();
	}

	public String getDatasetName(int datasetIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getDataset(datasetIndex).getName();
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
		// Parents: {u'Instrument': {u'OME': None}}
		// AmplificationGain is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getAmplificationGain();
	}

	public Double getDetectorGain(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Gain is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getGain();
	}

	public String getDetectorID(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getID();
	}

	// Ignoring Instrument_BackReference back reference
	public String getDetectorLotNumber(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getLotNumber();
	}

	public String getDetectorManufacturer(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getManufacturer();
	}

	public String getDetectorModel(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getModel();
	}

	public Double getDetectorOffset(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Offset is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getOffset();
	}

	public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getSerialNumber();
	}

	public DetectorType getDetectorType(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getType();
	}

	public Double getDetectorVoltage(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Voltage is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getVoltage();
	}

	public Double getDetectorZoom(int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Zoom is not a reference
		return root.getInstrument(instrumentIndex).getDetector(detectorIndex).getZoom();
	}

	//
	// DetectorSettings property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Binning is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getBinning();
	}

	// Ignoring DetectorRef back reference
	public Double getDetectorSettingsGain(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Gain is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getGain();
	}

	public String getDetectorSettingsID(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getID();
	}

	public Double getDetectorSettingsOffset(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Offset is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getOffset();
	}

	public Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ReadOutRate is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getReadOutRate();
	}

	public Double getDetectorSettingsVoltage(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Voltage is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getDetectorSettings().getVoltage();
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
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getID();
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getLotNumber();
	}

	public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getManufacturer();
	}

	public String getDichroicModel(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getModel();
	}

	public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getDichroic(dichroicIndex).getSerialNumber();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getDoubleAnnotationDescription(int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getDoubleAnnotationID(int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getDoubleAnnotationNamespace(int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex).getValue();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getEllipseFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getEllipseFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getEllipseFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getEllipseFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getEllipseID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getEllipseLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getEllipseLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getEllipseStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getEllipseText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getEllipseTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getEllipseTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getEllipseTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getEllipseTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getEllipseVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public Double getEllipseRadiusX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getRadiusX();
	}

	public Double getEllipseRadiusY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getRadiusY();
	}

	public Double getEllipseX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
	}

	public Double getEllipseY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Ellipse o = (Ellipse) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
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
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getExperiment(experimentIndex).getDescription();
	}

	public String getExperimentExperimenterRef(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getExperiment(experimentIndex).getLinkedExperimenter().getID();
	}

	public String getExperimentID(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getExperiment(experimentIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	public ExperimentType getExperimentType(int experimentIndex)
	{
		// Parents: {u'OME': None}
		// Type is not a reference
		return root.getExperiment(experimentIndex).getType();
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
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getExperimenter(experimenterIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Dataset_BackReference back reference
	public String getExperimenterEmail(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// Email is not a reference
		return root.getExperimenter(experimenterIndex).getEmail();
	}

	// Ignoring Experiment_BackReference back reference
	// Ignoring ExperimenterGroup_BackReference back reference
	public String getExperimenterFirstName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// FirstName is not a reference
		return root.getExperimenter(experimenterIndex).getFirstName();
	}

	public String getExperimenterID(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getExperimenter(experimenterIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getExperimenterInstitution(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// Institution is not a reference
		return root.getExperimenter(experimenterIndex).getInstitution();
	}

	public String getExperimenterLastName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// LastName is not a reference
		return root.getExperimenter(experimenterIndex).getLastName();
	}

	// Ignoring MicrobeamManipulation_BackReference back reference
	public String getExperimenterMiddleName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// MiddleName is not a reference
		return root.getExperimenter(experimenterIndex).getMiddleName();
	}

	// Ignoring Project_BackReference back reference
	public String getExperimenterUserName(int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// UserName is not a reference
		return root.getExperimenter(experimenterIndex).getUserName();
	}

	//
	// ExperimenterGroup property storage
	//
	// Indexes: {u'OME': [u'int experimenterGroupIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimenterGroupAnnotationRef(int experimenterGroupIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getExperimenterGroup(experimenterGroupIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Dataset_BackReference back reference
	public String getExperimenterGroupDescription(int experimenterGroupIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getExperimenterGroup(experimenterGroupIndex).getDescription();
	}

	public String getExperimenterGroupExperimenterRef(int experimenterGroupIndex, int experimenterRefIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs more than once
		return root.getExperimenterGroup(experimenterGroupIndex).getLinkedExperimenter(experimenterRefIndex).getID();
	}

	public String getExperimenterGroupID(int experimenterGroupIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getExperimenterGroup(experimenterGroupIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getExperimenterGroupLeader(int experimenterGroupIndex, int leaderIndex)
	{
		// Parents: {u'OME': None}
		// Leader is reference and occurs more than once
		return root.getExperimenterGroup(experimenterGroupIndex).getLinkedLeader(leaderIndex).getID();
	}

	public String getExperimenterGroupName(int experimenterGroupIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getExperimenterGroup(experimenterGroupIndex).getName();
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
	}

	// Manufacturer accessor from parent LightSource
	public String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
	}

	// Model accessor from parent LightSource
	public String getFilamentModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
	}

	// Power accessor from parent LightSource
	public Double getFilamentPower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
	}

	// SerialNumber accessor from parent LightSource
	public String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
	}

	public FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Filament o = (Filament) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getType();
	}

	//
	// FileAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int fileAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getFileAnnotationAnnotationRef(int fileAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getFileAnnotationDescription(int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getFileAnnotationID(int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getFileAnnotationNamespace(int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex).getNamespace();
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
		// Parents: {u'Instrument': {u'OME': None}}
		// FilterWheel is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getFilterWheel();
	}

	public String getFilterID(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getID();
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public String getFilterLotNumber(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getLotNumber();
	}

	public String getFilterManufacturer(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getManufacturer();
	}

	public String getFilterModel(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getModel();
	}

	public String getFilterSerialNumber(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getSerialNumber();
	}

	// Ignoring TransmittanceRange element, complex property
	public FilterType getFilterType(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getType();
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
		// Parents: {u'Instrument': {u'OME': None}}
		// DichroicRef is reference and occurs only once
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLinkedDichroic().getID();
	}

	public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// EmissionFilterRef is reference and occurs more than once
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLinkedEmissionFilter(emissionFilterRefIndex).getID();
	}

	public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ExcitationFilterRef is reference and occurs more than once
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLinkedExcitationFilter(excitationFilterRefIndex).getID();
	}

	public String getFilterSetID(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getID();
	}

	// Ignoring Instrument_BackReference back reference
	public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getLotNumber();
	}

	public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getManufacturer();
	}

	public String getFilterSetModel(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getModel();
	}

	public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex).getSerialNumber();
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
		// Parents: {u'OME': None}
		// AcquisitionDate is not a reference
		return root.getImage(imageIndex).getAcquisitionDate();
	}

	public String getImageAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Dataset_BackReference back reference
	public String getImageDescription(int imageIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getImage(imageIndex).getDescription();
	}

	public String getImageExperimentRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimentRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedExperiment().getID();
	}

	public String getImageExperimenterGroupRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterGroupRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedExperimenterGroup().getID();
	}

	public String getImageExperimenterRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedExperimenter().getID();
	}

	public String getImageID(int imageIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getImage(imageIndex).getID();
	}

	// Ignoring ImagingEnvironment element, complex property
	public String getImageInstrumentRef(int imageIndex)
	{
		// Parents: {u'OME': None}
		// InstrumentRef is reference and occurs only once
		return root.getImage(imageIndex).getLinkedInstrument().getID();
	}

	public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex)
	{
		// Parents: {u'OME': None}
		// MicrobeamManipulationRef is reference and occurs more than once
		return root.getImage(imageIndex).getLinkedMicrobeamManipulation(microbeamManipulationRefIndex).getID();
	}

	public String getImageName(int imageIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getImage(imageIndex).getName();
	}

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
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
		// Parents: {u'Image': {u'OME': None}}
		// AirPressure is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getAirPressure();
	}

	public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// CO2Percent is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getCO2Percent();
	}

	public PercentFraction getImagingEnvironmentHumidity(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Humidity is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getHumidity();
	}

	public Double getImagingEnvironmentTemperature(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Temperature is not a reference
		return root.getImage(imageIndex).getImagingEnvironment().getTemperature();
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
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getID();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getLabelFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getLabelFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getLabelFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getLabelFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getLabelID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getLabelLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getLabelLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getLabelStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getLabelStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getLabelStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getLabelText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getLabelTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getLabelTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getLabelTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getLabelTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getLabelVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public Double getLabelX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
	}

	public Double getLabelY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Label o = (Label) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLaserLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
	}

	// Manufacturer accessor from parent LightSource
	public String getLaserManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
	}

	// Model accessor from parent LightSource
	public String getLaserModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
	}

	// Power accessor from parent LightSource
	public Double getLaserPower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
	}

	// SerialNumber accessor from parent LightSource
	public String getLaserSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
	}

	public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getFrequencyMultiplication();
	}

	public LaserMedium getLaserLaserMedium(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLaserMedium();
	}

	public Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPockelCell();
	}

	public Pulse getLaserPulse(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPulse();
	}

	public String getLaserPump(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLinkedPump().getID();
	}

	public Double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getRepetitionRate();
	}

	public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getTuneable();
	}

	public LaserType getLaserType(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getType();
	}

	public PositiveInteger getLaserWavelength(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		Laser o = (Laser) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getWavelength();
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getID();
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getLotNumber();
	}

	// Manufacturer accessor from parent LightSource
	public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getManufacturer();
	}

	// Model accessor from parent LightSource
	public String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getModel();
	}

	// Power accessor from parent LightSource
	public Double getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getPower();
	}

	// SerialNumber accessor from parent LightSource
	public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		LightEmittingDiode o = (LightEmittingDiode) root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex);
		return o.getSerialNumber();
	}

	//
	// LightPath property storage
	//
	// Indexes: {u'Channel': [u'int imageIndex', u'int channelIndex']}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getLightPathDichroicRef(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// DichroicRef is reference and occurs only once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().getLinkedDichroic().getID();
	}

	public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// EmissionFilterRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath().getLinkedEmissionFilter(emissionFilterRefIndex).getID();
	}

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

	public PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Attenuation is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightSourceSettings().getAttenuation();
	}

	public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Attenuation is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLightSourceSettings(lightSourceSettingsIndex).getAttenuation();
	}

	public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightSourceSettings().getID();
	}

	public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ID is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLightSourceSettings(lightSourceSettingsIndex).getID();
	}

	// Ignoring LightSourceRef back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Wavelength is not a reference
		return root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightSourceSettings().getWavelength();
	}

	public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Wavelength is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLightSourceSettings(lightSourceSettingsIndex).getWavelength();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getLineFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getLineFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getLineFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getLineFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getLineID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getLineLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getLineLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getLineStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getLineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getLineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getLineText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getLineTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getLineTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getLineTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getLineTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getLineVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public Marker getLineMarkerEnd(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getMarkerEnd();
	}

	public Marker getLineMarkerStart(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getMarkerStart();
	}

	public Double getLineX1(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX1();
	}

	public Double getLineX2(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX2();
	}

	public Double getLineY1(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY1();
	}

	public Double getLineY2(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Line o = (Line) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY2();
	}

	//
	// ListAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [u'int listAnnotationIndex']}
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getListAnnotationDescription(int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getListAnnotationID(int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getListAnnotationNamespace(int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getLongAnnotationDescription(int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getLongAnnotationID(int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getLongAnnotationNamespace(int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex).getValue();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getMaskFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getMaskFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getMaskFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getMaskFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getMaskID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getMaskLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getMaskLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getMaskStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getMaskStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getMaskStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getMaskText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getMaskTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getMaskTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getMaskTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getMaskTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getMaskVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	// Ignoring BinData element, complex property
	public Double getMaskHeight(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getHeight();
	}

	public Double getMaskWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getWidth();
	}

	public Double getMaskX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
	}

	public Double getMaskY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Mask o = (Mask) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
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
		// Parents: {u'Experiment': {u'OME': None}}
		// Description is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getDescription();
	}

	// Ignoring Experiment_BackReference back reference
	public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ExperimenterRef is reference and occurs only once
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLinkedExperimenter().getID();
	}

	public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ID is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ROIRef is reference and occurs more than once
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getLinkedROI(ROIRefIndex).getID();
	}

	public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// Type is not a reference
		return root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex).getType();
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
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getLotNumber();
	}

	public String getMicroscopeManufacturer(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getManufacturer();
	}

	public String getMicroscopeModel(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getModel();
	}

	public String getMicroscopeSerialNumber(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getSerialNumber();
	}

	public MicroscopeType getMicroscopeType(int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		return root.getInstrument(instrumentIndex).getMicroscope().getType();
	}

	//
	// Objective property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int objectiveIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// CalibratedMagnification is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getCalibratedMagnification();
	}

	public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Correction is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getCorrection();
	}

	public String getObjectiveID(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getID();
	}

	public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Immersion is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getImmersion();
	}

	// Ignoring Instrument_BackReference back reference
	public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Iris is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getIris();
	}

	public Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LensNA is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getLensNA();
	}

	public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getLotNumber();
	}

	public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getManufacturer();
	}

	public String getObjectiveModel(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getModel();
	}

	public PositiveInteger getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// NominalMagnification is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getNominalMagnification();
	}

	public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getSerialNumber();
	}

	public Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// WorkingDistance is not a reference
		return root.getInstrument(instrumentIndex).getObjective(objectiveIndex).getWorkingDistance();
	}

	//
	// ObjectiveSettings property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public Double getObjectiveSettingsCorrectionCollar(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// CorrectionCollar is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getCorrectionCollar();
	}

	public String getObjectiveSettingsID(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// ID is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getID();
	}

	public Medium getObjectiveSettingsMedium(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Medium is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getMedium();
	}

	// Ignoring ObjectiveRef back reference
	public Double getObjectiveSettingsRefractiveIndex(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// RefractiveIndex is not a reference
		return root.getImage(imageIndex).getObjectiveSettings().getRefractiveIndex();
	}

	//
	// Pixels property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	public DimensionOrder getPixelsDimensionOrder(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// DimensionOrder is not a reference
		return root.getImage(imageIndex).getPixels().getDimensionOrder();
	}

	public String getPixelsID(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// ID is not a reference
		return root.getImage(imageIndex).getPixels().getID();
	}

	// Ignoring MetadataOnly element, complex property
	public PositiveFloat getPixelsPhysicalSizeX(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeX is not a reference
		return root.getImage(imageIndex).getPixels().getPhysicalSizeX();
	}

	public PositiveFloat getPixelsPhysicalSizeY(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeY is not a reference
		return root.getImage(imageIndex).getPixels().getPhysicalSizeY();
	}

	public PositiveFloat getPixelsPhysicalSizeZ(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeZ is not a reference
		return root.getImage(imageIndex).getPixels().getPhysicalSizeZ();
	}

	// Ignoring Plane element, complex property
	public PositiveInteger getPixelsSizeC(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeC is not a reference
		return root.getImage(imageIndex).getPixels().getSizeC();
	}

	public PositiveInteger getPixelsSizeT(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeT is not a reference
		return root.getImage(imageIndex).getPixels().getSizeT();
	}

	public PositiveInteger getPixelsSizeX(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeX is not a reference
		return root.getImage(imageIndex).getPixels().getSizeX();
	}

	public PositiveInteger getPixelsSizeY(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeY is not a reference
		return root.getImage(imageIndex).getPixels().getSizeY();
	}

	public PositiveInteger getPixelsSizeZ(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeZ is not a reference
		return root.getImage(imageIndex).getPixels().getSizeZ();
	}

	// Ignoring TiffData element, complex property
	public Double getPixelsTimeIncrement(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// TimeIncrement is not a reference
		return root.getImage(imageIndex).getPixels().getTimeIncrement();
	}

	public PixelType getPixelsType(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Type is not a reference
		return root.getImage(imageIndex).getPixels().getType();
	}

	//
	// Plane property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int planeIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public Double getPlaneDeltaT(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// DeltaT is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getDeltaT();
	}

	public Double getPlaneExposureTime(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ExposureTime is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getExposureTime();
	}

	public String getPlaneHashSHA1(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// HashSHA1 is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getHashSHA1();
	}

	// Ignoring Pixels_BackReference back reference
	public Double getPlanePositionX(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionX is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getPositionX();
	}

	public Double getPlanePositionY(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionY is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getPositionY();
	}

	public Double getPlanePositionZ(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionZ is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getPositionZ();
	}

	public NonNegativeInteger getPlaneTheC(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheC is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getTheC();
	}

	public NonNegativeInteger getPlaneTheT(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheT is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getTheT();
	}

	public NonNegativeInteger getPlaneTheZ(int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheZ is not a reference
		return root.getImage(imageIndex).getPixels().getPlane(planeIndex).getTheZ();
	}

	//
	// Plate property storage
	//
	// Indexes: {u'OME': [u'int plateIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getPlateAnnotationRef(int plateIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public NamingConvention getPlateColumnNamingConvention(int plateIndex)
	{
		// Parents: {u'OME': None}
		// ColumnNamingConvention is not a reference
		return root.getPlate(plateIndex).getColumnNamingConvention();
	}

	public PositiveInteger getPlateColumns(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Columns is not a reference
		return root.getPlate(plateIndex).getColumns();
	}

	public String getPlateDescription(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getPlate(plateIndex).getDescription();
	}

	public String getPlateExternalIdentifier(int plateIndex)
	{
		// Parents: {u'OME': None}
		// ExternalIdentifier is not a reference
		return root.getPlate(plateIndex).getExternalIdentifier();
	}

	public NonNegativeInteger getPlateFieldIndex(int plateIndex)
	{
		// Parents: {u'OME': None}
		// FieldIndex is not a reference
		return root.getPlate(plateIndex).getFieldIndex();
	}

	public String getPlateID(int plateIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getPlate(plateIndex).getID();
	}

	public String getPlateName(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getPlate(plateIndex).getName();
	}

	// Ignoring PlateAcquisition element, complex property
	public NamingConvention getPlateRowNamingConvention(int plateIndex)
	{
		// Parents: {u'OME': None}
		// RowNamingConvention is not a reference
		return root.getPlate(plateIndex).getRowNamingConvention();
	}

	public PositiveInteger getPlateRows(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Rows is not a reference
		return root.getPlate(plateIndex).getRows();
	}

	// Ignoring Screen_BackReference back reference
	public String getPlateStatus(int plateIndex)
	{
		// Parents: {u'OME': None}
		// Status is not a reference
		return root.getPlate(plateIndex).getStatus();
	}

	// Ignoring Well element, complex property
	public Double getPlateWellOriginX(int plateIndex)
	{
		// Parents: {u'OME': None}
		// WellOriginX is not a reference
		return root.getPlate(plateIndex).getWellOriginX();
	}

	public Double getPlateWellOriginY(int plateIndex)
	{
		// Parents: {u'OME': None}
		// WellOriginY is not a reference
		return root.getPlate(plateIndex).getWellOriginY();
	}

	//
	// PlateAcquisition property storage
	//
	// Indexes: {u'Plate': [u'int plateIndex', u'int plateAcquisitionIndex']}
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Description is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getDescription();
	}

	public Timestamp getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// EndTime is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getEndTime();
	}

	public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ID is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getID();
	}

	public PositiveInteger getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// MaximumFieldCount is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getMaximumFieldCount();
	}

	public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Name is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getName();
	}

	// Ignoring Plate_BackReference back reference
	public Timestamp getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// StartTime is not a reference
		return root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex).getStartTime();
	}

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

	// Ignoring Ellipse of parent abstract type
	// FillColor accessor from parent Shape
	public Color getPointFillColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getPointFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPointFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getPointFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPointFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getPointID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getPointLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getPointLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getPointStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getPointStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getPointStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getPointText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPointTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPointTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPointTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getPointTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getPointVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public Double getPointX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
	}

	public Double getPointY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Point o = (Point) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getPolygonFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPolygonFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getPolygonFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPolygonFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getPolygonID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getPolygonLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getPolygonLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getPolygonStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getPolygonStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getPolygonStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getPolygonText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPolygonTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPolygonTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPolygonTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getPolygonTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getPolygonVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public String getPolygonPoints(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polygon o = (Polygon) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getPoints();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getPolylineFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPolylineFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getPolylineFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPolylineFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getPolylineID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getPolylineLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getPolylineLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getPolylineStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getPolylineText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPolylineTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPolylineTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPolylineTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getPolylineTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getPolylineVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public Marker getPolylineMarkerEnd(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getMarkerEnd();
	}

	public Marker getPolylineMarkerStart(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getMarkerStart();
	}

	public String getPolylinePoints(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Polyline o = (Polyline) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getPoints();
	}

	//
	// Project property storage
	//
	// Indexes: {u'OME': [u'int projectIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getProjectAnnotationRef(int projectIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getProject(projectIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getProjectDatasetRef(int projectIndex, int datasetRefIndex)
	{
		// Parents: {u'OME': None}
		// DatasetRef is reference and occurs more than once
		return root.getProject(projectIndex).getLinkedDataset(datasetRefIndex).getID();
	}

	public String getProjectDescription(int projectIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getProject(projectIndex).getDescription();
	}

	public String getProjectExperimenterGroupRef(int projectIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterGroupRef is reference and occurs only once
		return root.getProject(projectIndex).getLinkedExperimenterGroup().getID();
	}

	public String getProjectExperimenterRef(int projectIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs only once
		return root.getProject(projectIndex).getLinkedExperimenter().getID();
	}

	public String getProjectID(int projectIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getProject(projectIndex).getID();
	}

	public String getProjectName(int projectIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getProject(projectIndex).getName();
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
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getROI(ROIIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getROIDescription(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getROI(ROIIndex).getDescription();
	}

	public String getROIID(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getROI(ROIIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public String getROIName(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getROI(ROIIndex).getName();
	}

	public String getROINamespace(int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Namespace is not a reference
		return root.getROI(ROIIndex).getNamespace();
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
		// Parents: {u'Screen': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getScreen(screenIndex).getReagent(reagentIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getReagentDescription(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Description is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getDescription();
	}

	public String getReagentID(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// ID is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getID();
	}

	public String getReagentName(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Name is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getName();
	}

	public String getReagentReagentIdentifier(int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// ReagentIdentifier is not a reference
		return root.getScreen(screenIndex).getReagent(reagentIndex).getReagentIdentifier();
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillColor();
	}

	// FillRule accessor from parent Shape
	public FillRule getRectangleFillRule(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFillRule();
	}

	// FontFamily accessor from parent Shape
	public FontFamily getRectangleFontFamily(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontFamily();
	}

	// FontSize accessor from parent Shape
	public NonNegativeInteger getRectangleFontSize(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontSize();
	}

	// FontStyle accessor from parent Shape
	public FontStyle getRectangleFontStyle(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getFontStyle();
	}

	// ID accessor from parent Shape
	public String getRectangleID(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getID();
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public LineCap getRectangleLineCap(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLineCap();
	}

	// Locked accessor from parent Shape
	public Boolean getRectangleLocked(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getLocked();
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public Color getRectangleStrokeColor(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeColor();
	}

	// StrokeDashArray accessor from parent Shape
	public String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeDashArray();
	}

	// StrokeWidth accessor from parent Shape
	public Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getStrokeWidth();
	}

	// Text accessor from parent Shape
	public String getRectangleText(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getText();
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getRectangleTheC(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheC();
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getRectangleTheT(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheT();
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getRectangleTheZ(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTheZ();
	}

	// Transform accessor from parent Shape
	public AffineTransform getRectangleTransform(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getTransform();
	}

	// Visible accessor from parent Shape
	public Boolean getRectangleVisible(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getVisible();
	}

	public Double getRectangleHeight(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getHeight();
	}

	public Double getRectangleWidth(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getWidth();
	}

	public Double getRectangleX(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getX();
	}

	public Double getRectangleY(int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		Rectangle o = (Rectangle) root.getROI(ROIIndex).getUnion().getShape(shapeIndex);
		return o.getY();
	}

	//
	// Screen property storage
	//
	// Indexes: {u'OME': [u'int screenIndex']}
	// {u'OME': None}
	// Is multi path? False

	public String getScreenAnnotationRef(int screenIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		return root.getScreen(screenIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getScreenDescription(int screenIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		return root.getScreen(screenIndex).getDescription();
	}

	public String getScreenID(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		return root.getScreen(screenIndex).getID();
	}

	public String getScreenName(int screenIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		return root.getScreen(screenIndex).getName();
	}

	public String getScreenPlateRef(int screenIndex, int plateRefIndex)
	{
		// Parents: {u'OME': None}
		// PlateRef is reference and occurs more than once
		return root.getScreen(screenIndex).getLinkedPlate(plateRefIndex).getID();
	}

	public String getScreenProtocolDescription(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ProtocolDescription is not a reference
		return root.getScreen(screenIndex).getProtocolDescription();
	}

	public String getScreenProtocolIdentifier(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ProtocolIdentifier is not a reference
		return root.getScreen(screenIndex).getProtocolIdentifier();
	}

	// Ignoring Reagent element, complex property
	public String getScreenReagentSetDescription(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ReagentSetDescription is not a reference
		return root.getScreen(screenIndex).getReagentSetDescription();
	}

	public String getScreenReagentSetIdentifier(int screenIndex)
	{
		// Parents: {u'OME': None}
		// ReagentSetIdentifier is not a reference
		return root.getScreen(screenIndex).getReagentSetIdentifier();
	}

	public String getScreenType(int screenIndex)
	{
		// Parents: {u'OME': None}
		// Type is not a reference
		return root.getScreen(screenIndex).getType();
	}

	//
	// StageLabel property storage
	//
	// Indexes: {u'Image': [u'int imageIndex']}
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public String getStageLabelName(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Name is not a reference
		return root.getImage(imageIndex).getStageLabel().getName();
	}

	public Double getStageLabelX(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// X is not a reference
		return root.getImage(imageIndex).getStageLabel().getX();
	}

	public Double getStageLabelY(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Y is not a reference
		return root.getImage(imageIndex).getStageLabel().getY();
	}

	public Double getStageLabelZ(int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Z is not a reference
		return root.getImage(imageIndex).getStageLabel().getZ();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTagAnnotationDescription(int tagAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getTagAnnotationID(int tagAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getTagAnnotationNamespace(int tagAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex).getValue();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTermAnnotationDescription(int termAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getTermAnnotationID(int termAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getTermAnnotationNamespace(int termAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex).getValue();
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
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstC is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getFirstC();
	}

	public NonNegativeInteger getTiffDataFirstT(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstT is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getFirstT();
	}

	public NonNegativeInteger getTiffDataFirstZ(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstZ is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getFirstZ();
	}

	public NonNegativeInteger getTiffDataIFD(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// IFD is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getIFD();
	}

	// Ignoring Pixels_BackReference back reference
	public NonNegativeInteger getTiffDataPlaneCount(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PlaneCount is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getPlaneCount();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTimestampAnnotationDescription(int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getTimestampAnnotationID(int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getTimestampAnnotationNamespace(int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex).getValue();
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
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutIn is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutIn();
	}

	public NonNegativeInteger getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutInTolerance is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutInTolerance();
	}

	public PositiveInteger getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutOut is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutOut();
	}

	public NonNegativeInteger getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutOutTolerance is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getCutOutTolerance();
	}

	public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// Transmittance is not a reference
		return root.getInstrument(instrumentIndex).getFilter(filterIndex).getTransmittanceRange().getTransmittance();
	}

	//
	// UUID property storage
	//
	// Indexes: {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getUUIDFileName(int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
		// FileName is not a reference
		return root.getImage(imageIndex).getPixels().getTiffData(tiffDataIndex).getUUID().getFileName();
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
		// Parents: {u'Plate': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getWell(wellIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public Color getWellColor(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Color is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getColor();
	}

	public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Column is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getColumn();
	}

	public String getWellExternalDescription(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ExternalDescription is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getExternalDescription();
	}

	public String getWellExternalIdentifier(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ExternalIdentifier is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getExternalIdentifier();
	}

	public String getWellID(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ID is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getID();
	}

	// Ignoring Plate_BackReference back reference
	public String getWellReagentRef(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ReagentRef is reference and occurs only once
		return root.getPlate(plateIndex).getWell(wellIndex).getLinkedReagent().getID();
	}

	public NonNegativeInteger getWellRow(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Row is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getRow();
	}

	public String getWellType(int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Type is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getType();
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
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// ID is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getID();
	}

	public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// ImageRef is reference and occurs only once
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getLinkedImage().getID();
	}

	public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// Index is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getIndex();
	}

	// Ignoring PlateAcquisition_BackReference back reference
	public Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// PositionX is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getPositionX();
	}

	public Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// PositionY is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getPositionY();
	}

	public Timestamp getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// Timepoint is not a reference
		return root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex).getTimepoint();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getLinkedAnnotation(annotationRefIndex).getID();
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getXMLAnnotationDescription(int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getDescription();
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public String getXMLAnnotationID(int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getID();
	}

	// Ignoring Image_BackReference back reference
	public String getXMLAnnotationNamespace(int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getNamespace();
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		return root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex).getValue();
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference

	// -- Entity storage (manual definitions) --

	public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfBinDataList() == binDataIndex)
		{
			o2.addBinData(new BinData());
		}
		BinData o3 = o2.getBinData(binDataIndex);
		o3.setBigEndian(bigEndian);
	}

	public void setMaskBinData(byte[] binData, int ROIIndex, int shapeIndex)
	{
		// TODO: To implement
	}

	// -- Entity storage (code generated definitions) --

	/** Sets the UUID associated with this collection of metadata. */
	public void setUUID(String uuid)
	{
		root.setUUID(uuid);
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		model.addModelObject(id, o2);
		((Arc)o2).setID(id);
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Arc)o2).setLotNumber(lotNumber);
	}

	// Manufacturer accessor from parent LightSource
	public void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Arc)o2).setManufacturer(manufacturer);
	}

	// Model accessor from parent LightSource
	public void setArcModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Arc)o2).setModel(model);
	}

	// Power accessor from parent LightSource
	public void setArcPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Arc)o2).setPower(power);
	}

	// SerialNumber accessor from parent LightSource
	public void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Arc)o2).setSerialNumber(serialNumber);
	}

	public void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Arc());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Arc)o2).setType(type);
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
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
		// FileName is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfFileAnnotationList() == fileAnnotationIndex)
		{
			o1.addFileAnnotation(new FileAnnotation());
		}
		FileAnnotation o2 = o1.getFileAnnotation(fileAnnotationIndex);
		if (o2.getBinaryFile() == null)
		{
			o2.setBinaryFile(new BinaryFile());
		}
		BinaryFile o3 = o2.getBinaryFile();
		o3.setFileName(fileName);
	}

	public void setBinaryFileMIMEType(String mimeType, int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
		// MIMEType is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfFileAnnotationList() == fileAnnotationIndex)
		{
			o1.addFileAnnotation(new FileAnnotation());
		}
		FileAnnotation o2 = o1.getFileAnnotation(fileAnnotationIndex);
		if (o2.getBinaryFile() == null)
		{
			o2.setBinaryFile(new BinaryFile());
		}
		BinaryFile o3 = o2.getBinaryFile();
		o3.setMIMEType(mimeType);
	}

	public void setBinaryFileSize(NonNegativeLong size, int fileAnnotationIndex)
	{
		// Parents: {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
		// Size is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfFileAnnotationList() == fileAnnotationIndex)
		{
			o1.addFileAnnotation(new FileAnnotation());
		}
		FileAnnotation o2 = o1.getFileAnnotation(fileAnnotationIndex);
		if (o2.getBinaryFile() == null)
		{
			o2.setBinaryFile(new BinaryFile());
		}
		BinaryFile o3 = o2.getBinaryFile();
		o3.setSize(size);
	}

	//
	// BinaryOnly property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setBinaryOnlyMetadataFile(String metadataFile)
	{
		// Parents: {u'OME': None}
		// MetadataFile is not a reference
		OME o0 = root;
		if (o0.getBinaryOnly() == null)
		{
			o0.setBinaryOnly(new BinaryOnly());
		}
		BinaryOnly o1 = o0.getBinaryOnly();
		o1.setMetadataFile(metadataFile);
	}

	public void setBinaryOnlyUUID(String uuid)
	{
		// Parents: {u'OME': None}
		// UUID is not a reference
		OME o0 = root;
		if (o0.getBinaryOnly() == null)
		{
			o0.setBinaryOnly(new BinaryOnly());
		}
		BinaryOnly o1 = o0.getBinaryOnly();
		o1.setUUID(uuid);
	}

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setBooleanAnnotationAnnotationRef(String annotation, int booleanAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getBooleanAnnotation(booleanAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setBooleanAnnotationDescription(String description, int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfBooleanAnnotationList() == booleanAnnotationIndex)
		{
			o1.addBooleanAnnotation(new BooleanAnnotation());
		}
		BooleanAnnotation o2 = o1.getBooleanAnnotation(booleanAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setBooleanAnnotationID(String id, int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfBooleanAnnotationList() == booleanAnnotationIndex)
		{
			o1.addBooleanAnnotation(new BooleanAnnotation());
		}
		BooleanAnnotation o2 = o1.getBooleanAnnotation(booleanAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfBooleanAnnotationList() == booleanAnnotationIndex)
		{
			o1.addBooleanAnnotation(new BooleanAnnotation());
		}
		BooleanAnnotation o2 = o1.getBooleanAnnotation(booleanAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfBooleanAnnotationList() == booleanAnnotationIndex)
		{
			o1.addBooleanAnnotation(new BooleanAnnotation());
		}
		BooleanAnnotation o2 = o1.getBooleanAnnotation(booleanAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AcquisitionMode is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setAcquisitionMode(acquisitionMode);
	}

	public void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getImage(imageIndex).getPixels().getChannel(channelIndex),
				annotationLinks_reference);
	}

	public void setChannelColor(Color color, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Color is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setColor(color);
	}

	public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ContrastMethod is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setContrastMethod(contrastMethod);
	}

	// Ignoring DetectorSettings element, complex property
	public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// EmissionWavelength is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setEmissionWavelength(emissionWavelength);
	}

	public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ExcitationWavelength is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setExcitationWavelength(excitationWavelength);
	}

	public void setChannelFilterSetRef(String filterSet, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FilterSetRef is reference and occurs more than once
		FilterSetRef filterSet_reference = new FilterSetRef();
		filterSet_reference.setID(filterSet);
		model.addReference(
				root.getImage(imageIndex).getPixels().getChannel(channelIndex),
				filterSet_reference);
	}

	public void setChannelFluor(String fluor, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Fluor is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setFluor(fluor);
	}

	public void setChannelID(String id, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		model.addModelObject(id, o3);
		o3.setID(id);
		// Custom content from Channel ID template
		if (o3.getLightPath() == null)
		{
			o3.setLightPath(new LightPath());
		}
	}

	public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// IlluminationType is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setIlluminationType(illuminationType);
	}

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// NDFilter is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setNDFilter(ndFilter);
	}

	public void setChannelName(String name, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setName(name);
	}

	public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PinholeSize is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setPinholeSize(pinholeSize);
	}

	// Ignoring Pixels_BackReference back reference
	public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PockelCellSetting is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setPockelCellSetting(pockelCellSetting);
	}

	public void setChannelSamplesPerPixel(PositiveInteger samplesPerPixel, int imageIndex, int channelIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// SamplesPerPixel is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		o3.setSamplesPerPixel(samplesPerPixel);
	}

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setCommentAnnotationAnnotationRef(String annotation, int commentAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getCommentAnnotation(commentAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setCommentAnnotationDescription(String description, int commentAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfCommentAnnotationList() == commentAnnotationIndex)
		{
			o1.addCommentAnnotation(new CommentAnnotation());
		}
		CommentAnnotation o2 = o1.getCommentAnnotation(commentAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setCommentAnnotationID(String id, int commentAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfCommentAnnotationList() == commentAnnotationIndex)
		{
			o1.addCommentAnnotation(new CommentAnnotation());
		}
		CommentAnnotation o2 = o1.getCommentAnnotation(commentAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setCommentAnnotationNamespace(String namespace, int commentAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfCommentAnnotationList() == commentAnnotationIndex)
		{
			o1.addCommentAnnotation(new CommentAnnotation());
		}
		CommentAnnotation o2 = o1.getCommentAnnotation(commentAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfCommentAnnotationList() == commentAnnotationIndex)
		{
			o1.addCommentAnnotation(new CommentAnnotation());
		}
		CommentAnnotation o2 = o1.getCommentAnnotation(commentAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getDataset(datasetIndex),
				annotationLinks_reference);
	}

	public void setDatasetDescription(String description, int datasetIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfDatasetList() == datasetIndex)
		{
			o0.addDataset(new Dataset());
		}
		Dataset o1 = o0.getDataset(datasetIndex);
		o1.setDescription(description);
	}

	public void setDatasetExperimenterGroupRef(String experimenterGroup, int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterGroupRef is reference and occurs more than once
		ExperimenterGroupRef experimenterGroup_reference = new ExperimenterGroupRef();
		experimenterGroup_reference.setID(experimenterGroup);
		model.addReference(
				root.getDataset(datasetIndex),
				experimenterGroup_reference);
	}

	public void setDatasetExperimenterRef(String experimenter, int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs more than once
		ExperimenterRef experimenter_reference = new ExperimenterRef();
		experimenter_reference.setID(experimenter);
		model.addReference(
				root.getDataset(datasetIndex),
				experimenter_reference);
	}

	public void setDatasetID(String id, int datasetIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfDatasetList() == datasetIndex)
		{
			o0.addDataset(new Dataset());
		}
		Dataset o1 = o0.getDataset(datasetIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	public void setDatasetImageRef(String image, int datasetIndex, int imageRefIndex)
	{
		// Parents: {u'OME': None}
		// ImageRef is reference and occurs more than once
		ImageRef imageLinks_reference = new ImageRef();
		imageLinks_reference.setID(image);
		model.addReference(
				root.getDataset(datasetIndex),
				imageLinks_reference);
	}

	public void setDatasetName(String name, int datasetIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfDatasetList() == datasetIndex)
		{
			o0.addDataset(new Dataset());
		}
		Dataset o1 = o0.getDataset(datasetIndex);
		o1.setName(name);
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
		// Parents: {u'Instrument': {u'OME': None}}
		// AmplificationGain is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setAmplificationGain(amplificationGain);
	}

	public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Gain is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setGain(gain);
	}

	public void setDetectorID(String id, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Instrument_BackReference back reference
	public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setLotNumber(lotNumber);
	}

	public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setManufacturer(manufacturer);
	}

	public void setDetectorModel(String model, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setModel(model);
	}

	public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Offset is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setOffset(offset);
	}

	public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setSerialNumber(serialNumber);
	}

	public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setType(type);
	}

	public void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Voltage is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setVoltage(voltage);
	}

	public void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Zoom is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDetectorList() == detectorIndex)
		{
			o1.addDetector(new Detector());
		}
		Detector o2 = o1.getDetector(detectorIndex);
		o2.setZoom(zoom);
	}

	//
	// DetectorSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Binning is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getDetectorSettings() == null)
		{
			o3.setDetectorSettings(new DetectorSettings());
		}
		DetectorSettings o4 = o3.getDetectorSettings();
		o4.setBinning(binning);
	}

	// Ignoring DetectorRef back reference
	public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Gain is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getDetectorSettings() == null)
		{
			o3.setDetectorSettings(new DetectorSettings());
		}
		DetectorSettings o4 = o3.getDetectorSettings();
		o4.setGain(gain);
	}

	public void setDetectorSettingsID(String id, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getDetectorSettings() == null)
		{
			o3.setDetectorSettings(new DetectorSettings());
		}
		DetectorSettings o4 = o3.getDetectorSettings();
		model.addModelObject(id, o4);
		o4.setID(id);
	}

	public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Offset is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getDetectorSettings() == null)
		{
			o3.setDetectorSettings(new DetectorSettings());
		}
		DetectorSettings o4 = o3.getDetectorSettings();
		o4.setOffset(offset);
	}

	public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ReadOutRate is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getDetectorSettings() == null)
		{
			o3.setDetectorSettings(new DetectorSettings());
		}
		DetectorSettings o4 = o3.getDetectorSettings();
		o4.setReadOutRate(readOutRate);
	}

	public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// Voltage is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getDetectorSettings() == null)
		{
			o3.setDetectorSettings(new DetectorSettings());
		}
		DetectorSettings o4 = o3.getDetectorSettings();
		o4.setVoltage(voltage);
	}

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public void setDichroicID(String id, int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDichroicList() == dichroicIndex)
		{
			o1.addDichroic(new Dichroic());
		}
		Dichroic o2 = o1.getDichroic(dichroicIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDichroicList() == dichroicIndex)
		{
			o1.addDichroic(new Dichroic());
		}
		Dichroic o2 = o1.getDichroic(dichroicIndex);
		o2.setLotNumber(lotNumber);
	}

	public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDichroicList() == dichroicIndex)
		{
			o1.addDichroic(new Dichroic());
		}
		Dichroic o2 = o1.getDichroic(dichroicIndex);
		o2.setManufacturer(manufacturer);
	}

	public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDichroicList() == dichroicIndex)
		{
			o1.addDichroic(new Dichroic());
		}
		Dichroic o2 = o1.getDichroic(dichroicIndex);
		o2.setModel(model);
	}

	public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfDichroicList() == dichroicIndex)
		{
			o1.addDichroic(new Dichroic());
		}
		Dichroic o2 = o1.getDichroic(dichroicIndex);
		o2.setSerialNumber(serialNumber);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getDoubleAnnotation(doubleAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setDoubleAnnotationDescription(String description, int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfDoubleAnnotationList() == doubleAnnotationIndex)
		{
			o1.addDoubleAnnotation(new DoubleAnnotation());
		}
		DoubleAnnotation o2 = o1.getDoubleAnnotation(doubleAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setDoubleAnnotationID(String id, int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfDoubleAnnotationList() == doubleAnnotationIndex)
		{
			o1.addDoubleAnnotation(new DoubleAnnotation());
		}
		DoubleAnnotation o2 = o1.getDoubleAnnotation(doubleAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfDoubleAnnotationList() == doubleAnnotationIndex)
		{
			o1.addDoubleAnnotation(new DoubleAnnotation());
		}
		DoubleAnnotation o2 = o1.getDoubleAnnotation(doubleAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfDoubleAnnotationList() == doubleAnnotationIndex)
		{
			o1.addDoubleAnnotation(new DoubleAnnotation());
		}
		DoubleAnnotation o2 = o1.getDoubleAnnotation(doubleAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setEllipseFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setEllipseFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setEllipseFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setEllipseFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setEllipseID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Ellipse)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setEllipseLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setEllipseLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setEllipseStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setEllipseStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setEllipseText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setEllipseTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setEllipseTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setEllipseTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setEllipseTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setEllipseVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setVisible(visible);
	}

	public void setEllipseRadiusX(Double radiusX, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setRadiusX(radiusX);
	}

	public void setEllipseRadiusY(Double radiusY, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setRadiusY(radiusY);
	}

	public void setEllipseX(Double x, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setX(x);
	}

	public void setEllipseY(Double y, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Ellipse());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Ellipse)o3).setY(y);
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
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		o1.setDescription(description);
	}

	public void setExperimentExperimenterRef(String experimenter, int experimentIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs more than once
		ExperimenterRef experimenter_reference = new ExperimenterRef();
		experimenter_reference.setID(experimenter);
		model.addReference(
				root.getExperiment(experimentIndex),
				experimenter_reference);
	}

	public void setExperimentID(String id, int experimentIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	public void setExperimentType(ExperimentType type, int experimentIndex)
	{
		// Parents: {u'OME': None}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		o1.setType(type);
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
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getExperimenter(experimenterIndex),
				annotationLinks_reference);
	}

	// Ignoring Dataset_BackReference back reference
	public void setExperimenterEmail(String email, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// Email is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		o1.setEmail(email);
	}

	// Ignoring Experiment_BackReference back reference
	// Ignoring ExperimenterGroup_BackReference back reference
	public void setExperimenterFirstName(String firstName, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// FirstName is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		o1.setFirstName(firstName);
	}

	public void setExperimenterID(String id, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setExperimenterInstitution(String institution, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// Institution is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		o1.setInstitution(institution);
	}

	public void setExperimenterLastName(String lastName, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// LastName is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		o1.setLastName(lastName);
	}

	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setExperimenterMiddleName(String middleName, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// MiddleName is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		o1.setMiddleName(middleName);
	}

	// Ignoring Project_BackReference back reference
	public void setExperimenterUserName(String userName, int experimenterIndex)
	{
		// Parents: {u'OME': None}
		// UserName is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterList() == experimenterIndex)
		{
			o0.addExperimenter(new Experimenter());
		}
		Experimenter o1 = o0.getExperimenter(experimenterIndex);
		o1.setUserName(userName);
	}

	//
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setExperimenterGroupAnnotationRef(String annotation, int experimenterGroupIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getExperimenterGroup(experimenterGroupIndex),
				annotationLinks_reference);
	}

	// Ignoring Dataset_BackReference back reference
	public void setExperimenterGroupDescription(String description, int experimenterGroupIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterGroupList() == experimenterGroupIndex)
		{
			o0.addExperimenterGroup(new ExperimenterGroup());
		}
		ExperimenterGroup o1 = o0.getExperimenterGroup(experimenterGroupIndex);
		o1.setDescription(description);
	}

	public void setExperimenterGroupExperimenterRef(String experimenter, int experimenterGroupIndex, int experimenterRefIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs more than once
		ExperimenterRef experimenterLinks_reference = new ExperimenterRef();
		experimenterLinks_reference.setID(experimenter);
		model.addReference(
				root.getExperimenterGroup(experimenterGroupIndex),
				experimenterLinks_reference);
	}

	public void setExperimenterGroupID(String id, int experimenterGroupIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterGroupList() == experimenterGroupIndex)
		{
			o0.addExperimenterGroup(new ExperimenterGroup());
		}
		ExperimenterGroup o1 = o0.getExperimenterGroup(experimenterGroupIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setExperimenterGroupLeader(String leader, int experimenterGroupIndex, int leaderIndex)
	{
		// Parents: {u'OME': None}
		// Leader is reference and occurs more than once
		Leader leaders_reference = new Leader();
		leaders_reference.setID(leader);
		model.addReference(
				root.getExperimenterGroup(experimenterGroupIndex),
				leaders_reference);
	}

	public void setExperimenterGroupName(String name, int experimenterGroupIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimenterGroupList() == experimenterGroupIndex)
		{
			o0.addExperimenterGroup(new ExperimenterGroup());
		}
		ExperimenterGroup o1 = o0.getExperimenterGroup(experimenterGroupIndex);
		o1.setName(name);
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		model.addModelObject(id, o2);
		((Filament)o2).setID(id);
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Filament)o2).setLotNumber(lotNumber);
	}

	// Manufacturer accessor from parent LightSource
	public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Filament)o2).setManufacturer(manufacturer);
	}

	// Model accessor from parent LightSource
	public void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Filament)o2).setModel(model);
	}

	// Power accessor from parent LightSource
	public void setFilamentPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Filament)o2).setPower(power);
	}

	// SerialNumber accessor from parent LightSource
	public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Filament)o2).setSerialNumber(serialNumber);
	}

	public void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Filament());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Filament)o2).setType(type);
	}

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setFileAnnotationAnnotationRef(String annotation, int fileAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getFileAnnotation(fileAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setFileAnnotationDescription(String description, int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfFileAnnotationList() == fileAnnotationIndex)
		{
			o1.addFileAnnotation(new FileAnnotation());
		}
		FileAnnotation o2 = o1.getFileAnnotation(fileAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setFileAnnotationID(String id, int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfFileAnnotationList() == fileAnnotationIndex)
		{
			o1.addFileAnnotation(new FileAnnotation());
		}
		FileAnnotation o2 = o1.getFileAnnotation(fileAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfFileAnnotationList() == fileAnnotationIndex)
		{
			o1.addFileAnnotation(new FileAnnotation());
		}
		FileAnnotation o2 = o1.getFileAnnotation(fileAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'Instrument': {u'OME': None}}
		// FilterWheel is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		o2.setFilterWheel(filterWheel);
	}

	public void setFilterID(String id, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Instrument_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	// Ignoring LightPath_BackReference back reference
	public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		o2.setLotNumber(lotNumber);
	}

	public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		o2.setManufacturer(manufacturer);
	}

	public void setFilterModel(String model, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		o2.setModel(model);
	}

	public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		o2.setSerialNumber(serialNumber);
	}

	// Ignoring TransmittanceRange element, complex property
	public void setFilterType(FilterType type, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		o2.setType(type);
	}

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	public void setFilterSetDichroicRef(String dichroic, int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// DichroicRef is reference and occurs more than once
		DichroicRef dichroic_reference = new DichroicRef();
		dichroic_reference.setID(dichroic);
		model.addReference(
				root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex),
				dichroic_reference);
	}

	public void setFilterSetEmissionFilterRef(String emissionFilter, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// EmissionFilterRef is reference and occurs more than once
		EmissionFilterRef emissionFilterLinks_reference = new EmissionFilterRef();
		emissionFilterLinks_reference.setID(emissionFilter);
		model.addReference(
				root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex),
				emissionFilterLinks_reference);
	}

	public void setFilterSetExcitationFilterRef(String excitationFilter, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ExcitationFilterRef is reference and occurs more than once
		ExcitationFilterRef excitationFilterLinks_reference = new ExcitationFilterRef();
		excitationFilterLinks_reference.setID(excitationFilter);
		model.addReference(
				root.getInstrument(instrumentIndex).getFilterSet(filterSetIndex),
				excitationFilterLinks_reference);
	}

	public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterSetList() == filterSetIndex)
		{
			o1.addFilterSet(new FilterSet());
		}
		FilterSet o2 = o1.getFilterSet(filterSetIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Instrument_BackReference back reference
	public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterSetList() == filterSetIndex)
		{
			o1.addFilterSet(new FilterSet());
		}
		FilterSet o2 = o1.getFilterSet(filterSetIndex);
		o2.setLotNumber(lotNumber);
	}

	public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterSetList() == filterSetIndex)
		{
			o1.addFilterSet(new FilterSet());
		}
		FilterSet o2 = o1.getFilterSet(filterSetIndex);
		o2.setManufacturer(manufacturer);
	}

	public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterSetList() == filterSetIndex)
		{
			o1.addFilterSet(new FilterSet());
		}
		FilterSet o2 = o1.getFilterSet(filterSetIndex);
		o2.setModel(model);
	}

	public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterSetList() == filterSetIndex)
		{
			o1.addFilterSet(new FilterSet());
		}
		FilterSet o2 = o1.getFilterSet(filterSetIndex);
		o2.setSerialNumber(serialNumber);
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
		// Parents: {u'OME': None}
		// AcquisitionDate is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		o1.setAcquisitionDate(acquisitionDate);
	}

	public void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getImage(imageIndex),
				annotationLinks_reference);
	}

	// Ignoring Dataset_BackReference back reference
	public void setImageDescription(String description, int imageIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		o1.setDescription(description);
	}

	public void setImageExperimentRef(String experiment, int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimentRef is reference and occurs more than once
		ExperimentRef experiment_reference = new ExperimentRef();
		experiment_reference.setID(experiment);
		model.addReference(
				root.getImage(imageIndex),
				experiment_reference);
	}

	public void setImageExperimenterGroupRef(String experimenterGroup, int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterGroupRef is reference and occurs more than once
		ExperimenterGroupRef experimenterGroup_reference = new ExperimenterGroupRef();
		experimenterGroup_reference.setID(experimenterGroup);
		model.addReference(
				root.getImage(imageIndex),
				experimenterGroup_reference);
	}

	public void setImageExperimenterRef(String experimenter, int imageIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs more than once
		ExperimenterRef experimenter_reference = new ExperimenterRef();
		experimenter_reference.setID(experimenter);
		model.addReference(
				root.getImage(imageIndex),
				experimenter_reference);
	}

	public void setImageID(String id, int imageIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	// Ignoring ImagingEnvironment element, complex property
	public void setImageInstrumentRef(String instrument, int imageIndex)
	{
		// Parents: {u'OME': None}
		// InstrumentRef is reference and occurs more than once
		InstrumentRef instrument_reference = new InstrumentRef();
		instrument_reference.setID(instrument);
		model.addReference(
				root.getImage(imageIndex),
				instrument_reference);
	}

	public void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex)
	{
		// Parents: {u'OME': None}
		// MicrobeamManipulationRef is reference and occurs more than once
		MicrobeamManipulationRef microbeamManipulationLinks_reference = new MicrobeamManipulationRef();
		microbeamManipulationLinks_reference.setID(microbeamManipulation);
		model.addReference(
				root.getImage(imageIndex),
				microbeamManipulationLinks_reference);
	}

	public void setImageName(String name, int imageIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		o1.setName(name);
	}

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	public void setImageROIRef(String roi, int imageIndex, int ROIRefIndex)
	{
		// Parents: {u'OME': None}
		// ROIRef is reference and occurs more than once
		ROIRef roiLinks_reference = new ROIRef();
		roiLinks_reference.setID(roi);
		model.addReference(
				root.getImage(imageIndex),
				roiLinks_reference);
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
		// Parents: {u'Image': {u'OME': None}}
		// AirPressure is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getImagingEnvironment() == null)
		{
			o1.setImagingEnvironment(new ImagingEnvironment());
		}
		ImagingEnvironment o2 = o1.getImagingEnvironment();
		o2.setAirPressure(airPressure);
	}

	public void setImagingEnvironmentCO2Percent(PercentFraction co2Percent, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// CO2Percent is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getImagingEnvironment() == null)
		{
			o1.setImagingEnvironment(new ImagingEnvironment());
		}
		ImagingEnvironment o2 = o1.getImagingEnvironment();
		o2.setCO2Percent(co2Percent);
	}

	public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Humidity is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getImagingEnvironment() == null)
		{
			o1.setImagingEnvironment(new ImagingEnvironment());
		}
		ImagingEnvironment o2 = o1.getImagingEnvironment();
		o2.setHumidity(humidity);
	}

	public void setImagingEnvironmentTemperature(Double temperature, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Temperature is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getImagingEnvironment() == null)
		{
			o1.setImagingEnvironment(new ImagingEnvironment());
		}
		ImagingEnvironment o2 = o1.getImagingEnvironment();
		o2.setTemperature(temperature);
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
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setLabelFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setLabelFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setLabelFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setLabelFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setLabelID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Label)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setLabelLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setLabelLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setLabelStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setLabelStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setLabelStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setLabelText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setLabelTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setLabelTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setLabelTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setLabelTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setLabelVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setVisible(visible);
	}

	public void setLabelX(Double x, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setX(x);
	}

	public void setLabelY(Double y, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Label());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Label)o3).setY(y);
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		model.addModelObject(id, o2);
		((Laser)o2).setID(id);
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setLotNumber(lotNumber);
	}

	// Manufacturer accessor from parent LightSource
	public void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setManufacturer(manufacturer);
	}

	// Model accessor from parent LightSource
	public void setLaserModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setModel(model);
	}

	// Power accessor from parent LightSource
	public void setLaserPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setPower(power);
	}

	// SerialNumber accessor from parent LightSource
	public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setSerialNumber(serialNumber);
	}

	public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setFrequencyMultiplication(frequencyMultiplication);
	}

	public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setLaserMedium(laserMedium);
	}

	public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setPockelCell(pockelCell);
	}

	public void setLaserPulse(Pulse pulse, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setPulse(pulse);
	}

	public void setLaserPump(String pump, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// Pump is abstract proprietary and is a reference
		Pump pump_reference = new Pump();
		pump_reference.setID(pump);
		model.addReference(
				root.getInstrument(instrumentIndex).getLightSource(lightSourceIndex),
				pump_reference);
		// LightSource is abstract proprietary
	}

	public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setRepetitionRate(repetitionRate);
	}

	public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setTuneable(tuneable);
	}

	public void setLaserType(LaserType type, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setType(type);
	}

	public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new Laser());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((Laser)o2).setWavelength(wavelength);
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
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new LightEmittingDiode());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		model.addModelObject(id, o2);
		((LightEmittingDiode)o2).setID(id);
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new LightEmittingDiode());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((LightEmittingDiode)o2).setLotNumber(lotNumber);
	}

	// Manufacturer accessor from parent LightSource
	public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new LightEmittingDiode());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((LightEmittingDiode)o2).setManufacturer(manufacturer);
	}

	// Model accessor from parent LightSource
	public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new LightEmittingDiode());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((LightEmittingDiode)o2).setModel(model);
	}

	// Power accessor from parent LightSource
	public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new LightEmittingDiode());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((LightEmittingDiode)o2).setPower(power);
	}

	// SerialNumber accessor from parent LightSource
	public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		// Parents: {u'LightSource': {u'Instrument': {u'OME': None}}}
		// LightSource is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfLightSourceList() == lightSourceIndex)
		{
			o1.addLightSource(new LightEmittingDiode());
		}
		LightSource o2 = o1.getLightSource(lightSourceIndex);
		((LightEmittingDiode)o2).setSerialNumber(serialNumber);
	}

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setLightPathDichroicRef(String dichroic, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// DichroicRef is reference and occurs more than once
		DichroicRef dichroic_reference = new DichroicRef();
		dichroic_reference.setID(dichroic);
		model.addReference(
				root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath(),
				dichroic_reference);
	}

	public void setLightPathEmissionFilterRef(String emissionFilter, int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// EmissionFilterRef is reference and occurs more than once
		EmissionFilterRef emissionFilterLinks_reference = new EmissionFilterRef();
		emissionFilterLinks_reference.setID(emissionFilter);
		model.addReference(
				root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath(),
				emissionFilterLinks_reference);
	}

	public void setLightPathExcitationFilterRef(String excitationFilter, int imageIndex, int channelIndex, int excitationFilterRefIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
		// ExcitationFilterRef is reference and occurs more than once
		ExcitationFilterRef excitationFilterLinks_reference = new ExcitationFilterRef();
		excitationFilterLinks_reference.setID(excitationFilter);
		model.addReference(
				root.getImage(imageIndex).getPixels().getChannel(channelIndex).getLightPath(),
				excitationFilterLinks_reference);
	}

	//
	// LightSourceSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	public void setChannelLightSourceSettingsAttenuation(PercentFraction attenuation, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Attenuation is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getLightSourceSettings() == null)
		{
			o3.setLightSourceSettings(new LightSourceSettings());
		}
		LightSourceSettings o4 = o3.getLightSourceSettings();
		o4.setAttenuation(attenuation);
	}

	public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Attenuation is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		if (o1.sizeOfMicrobeamManipulationList() == microbeamManipulationIndex)
		{
			o1.addMicrobeamManipulation(new MicrobeamManipulation());
		}
		MicrobeamManipulation o2 = o1.getMicrobeamManipulation(microbeamManipulationIndex);
		if (o2.sizeOfLightSourceSettingsList() == lightSourceSettingsIndex)
		{
			o2.addLightSourceSettings(new LightSourceSettings());
		}
		LightSourceSettings o3 = o2.getLightSourceSettings(lightSourceSettingsIndex);
		o3.setAttenuation(attenuation);
	}

	public void setChannelLightSourceSettingsID(String id, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getLightSourceSettings() == null)
		{
			o3.setLightSourceSettings(new LightSourceSettings());
		}
		LightSourceSettings o4 = o3.getLightSourceSettings();
		model.addModelObject(id, o4);
		o4.setID(id);
	}

	public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		if (o1.sizeOfMicrobeamManipulationList() == microbeamManipulationIndex)
		{
			o1.addMicrobeamManipulation(new MicrobeamManipulation());
		}
		MicrobeamManipulation o2 = o1.getMicrobeamManipulation(microbeamManipulationIndex);
		if (o2.sizeOfLightSourceSettingsList() == lightSourceSettingsIndex)
		{
			o2.addLightSourceSettings(new LightSourceSettings());
		}
		LightSourceSettings o3 = o2.getLightSourceSettings(lightSourceSettingsIndex);
		model.addModelObject(id, o3);
		o3.setID(id);
	}

	// Ignoring LightSourceRef back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setChannelLightSourceSettingsWavelength(PositiveInteger wavelength, int imageIndex, int channelIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Wavelength is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfChannelList() == channelIndex)
		{
			o2.addChannel(new Channel());
		}
		Channel o3 = o2.getChannel(channelIndex);
		if (o3.getLightSourceSettings() == null)
		{
			o3.setLightSourceSettings(new LightSourceSettings());
		}
		LightSourceSettings o4 = o3.getLightSourceSettings();
		o4.setWavelength(wavelength);
	}

	public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		// Parents: {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
		// Wavelength is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		if (o1.sizeOfMicrobeamManipulationList() == microbeamManipulationIndex)
		{
			o1.addMicrobeamManipulation(new MicrobeamManipulation());
		}
		MicrobeamManipulation o2 = o1.getMicrobeamManipulation(microbeamManipulationIndex);
		if (o2.sizeOfLightSourceSettingsList() == lightSourceSettingsIndex)
		{
			o2.addLightSourceSettings(new LightSourceSettings());
		}
		LightSourceSettings o3 = o2.getLightSourceSettings(lightSourceSettingsIndex);
		o3.setWavelength(wavelength);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setLineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setLineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setLineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setLineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setLineID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Line)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setLineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setLineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setLineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setLineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setLineText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setLineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setLineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setLineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setLineTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setLineVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setVisible(visible);
	}

	public void setLineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setMarkerEnd(markerEnd);
	}

	public void setLineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setMarkerStart(markerStart);
	}

	public void setLineX1(Double x1, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setX1(x1);
	}

	public void setLineX2(Double x2, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setX2(x2);
	}

	public void setLineY1(Double y1, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setY1(y1);
	}

	public void setLineY2(Double y2, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Line());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Line)o3).setY2(y2);
	}

	//
	// ListAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setListAnnotationAnnotationRef(String annotation, int listAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getListAnnotation(listAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setListAnnotationDescription(String description, int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfListAnnotationList() == listAnnotationIndex)
		{
			o1.addListAnnotation(new ListAnnotation());
		}
		ListAnnotation o2 = o1.getListAnnotation(listAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setListAnnotationID(String id, int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfListAnnotationList() == listAnnotationIndex)
		{
			o1.addListAnnotation(new ListAnnotation());
		}
		ListAnnotation o2 = o1.getListAnnotation(listAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setListAnnotationNamespace(String namespace, int listAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfListAnnotationList() == listAnnotationIndex)
		{
			o1.addListAnnotation(new ListAnnotation());
		}
		ListAnnotation o2 = o1.getListAnnotation(listAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getLongAnnotation(longAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setLongAnnotationDescription(String description, int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfLongAnnotationList() == longAnnotationIndex)
		{
			o1.addLongAnnotation(new LongAnnotation());
		}
		LongAnnotation o2 = o1.getLongAnnotation(longAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setLongAnnotationID(String id, int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfLongAnnotationList() == longAnnotationIndex)
		{
			o1.addLongAnnotation(new LongAnnotation());
		}
		LongAnnotation o2 = o1.getLongAnnotation(longAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfLongAnnotationList() == longAnnotationIndex)
		{
			o1.addLongAnnotation(new LongAnnotation());
		}
		LongAnnotation o2 = o1.getLongAnnotation(longAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfLongAnnotationList() == longAnnotationIndex)
		{
			o1.addLongAnnotation(new LongAnnotation());
		}
		LongAnnotation o2 = o1.getLongAnnotation(longAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setMaskFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setMaskFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setMaskFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setMaskFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setMaskID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Mask)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setMaskLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setMaskLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setMaskStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setMaskStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setMaskText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setMaskTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setMaskTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setMaskTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setMaskTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setMaskVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setVisible(visible);
	}

	// Ignoring BinData element, complex property
	public void setMaskHeight(Double height, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setHeight(height);
	}

	public void setMaskWidth(Double width, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setWidth(width);
	}

	public void setMaskX(Double x, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setX(x);
	}

	public void setMaskY(Double y, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Mask());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Mask)o3).setY(y);
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
		// Parents: {u'Experiment': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		if (o1.sizeOfMicrobeamManipulationList() == microbeamManipulationIndex)
		{
			o1.addMicrobeamManipulation(new MicrobeamManipulation());
		}
		MicrobeamManipulation o2 = o1.getMicrobeamManipulation(microbeamManipulationIndex);
		o2.setDescription(description);
	}

	// Ignoring Experiment_BackReference back reference
	public void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ExperimenterRef is reference and occurs more than once
		ExperimenterRef experimenter_reference = new ExperimenterRef();
		experimenter_reference.setID(experimenter);
		model.addReference(
				root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex),
				experimenter_reference);
	}

	public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		if (o1.sizeOfMicrobeamManipulationList() == microbeamManipulationIndex)
		{
			o1.addMicrobeamManipulation(new MicrobeamManipulation());
		}
		MicrobeamManipulation o2 = o1.getMicrobeamManipulation(microbeamManipulationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	public void setMicrobeamManipulationROIRef(String roi, int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// ROIRef is reference and occurs more than once
		ROIRef roiLinks_reference = new ROIRef();
		roiLinks_reference.setID(roi);
		model.addReference(
				root.getExperiment(experimentIndex).getMicrobeamManipulation(microbeamManipulationIndex),
				roiLinks_reference);
	}

	public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex)
	{
		// Parents: {u'Experiment': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfExperimentList() == experimentIndex)
		{
			o0.addExperiment(new Experiment());
		}
		Experiment o1 = o0.getExperiment(experimentIndex);
		if (o1.sizeOfMicrobeamManipulationList() == microbeamManipulationIndex)
		{
			o1.addMicrobeamManipulation(new MicrobeamManipulation());
		}
		MicrobeamManipulation o2 = o1.getMicrobeamManipulation(microbeamManipulationIndex);
		o2.setType(type);
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
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.getMicroscope() == null)
		{
			o1.setMicroscope(new Microscope());
		}
		Microscope o2 = o1.getMicroscope();
		o2.setLotNumber(lotNumber);
	}

	public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.getMicroscope() == null)
		{
			o1.setMicroscope(new Microscope());
		}
		Microscope o2 = o1.getMicroscope();
		o2.setManufacturer(manufacturer);
	}

	public void setMicroscopeModel(String model, int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.getMicroscope() == null)
		{
			o1.setMicroscope(new Microscope());
		}
		Microscope o2 = o1.getMicroscope();
		o2.setModel(model);
	}

	public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.getMicroscope() == null)
		{
			o1.setMicroscope(new Microscope());
		}
		Microscope o2 = o1.getMicroscope();
		o2.setSerialNumber(serialNumber);
	}

	public void setMicroscopeType(MicroscopeType type, int instrumentIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.getMicroscope() == null)
		{
			o1.setMicroscope(new Microscope());
		}
		Microscope o2 = o1.getMicroscope();
		o2.setType(type);
	}

	//
	// Objective property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// CalibratedMagnification is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setCalibratedMagnification(calibratedMagnification);
	}

	public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Correction is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setCorrection(correction);
	}

	public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Immersion is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setImmersion(immersion);
	}

	// Ignoring Instrument_BackReference back reference
	public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Iris is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setIris(iris);
	}

	public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LensNA is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setLensNA(lensNA);
	}

	public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// LotNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setLotNumber(lotNumber);
	}

	public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Manufacturer is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setManufacturer(manufacturer);
	}

	public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// Model is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setModel(model);
	}

	public void setObjectiveNominalMagnification(PositiveInteger nominalMagnification, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// NominalMagnification is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setNominalMagnification(nominalMagnification);
	}

	public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// SerialNumber is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setSerialNumber(serialNumber);
	}

	public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex)
	{
		// Parents: {u'Instrument': {u'OME': None}}
		// WorkingDistance is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfObjectiveList() == objectiveIndex)
		{
			o1.addObjective(new Objective());
		}
		Objective o2 = o1.getObjective(objectiveIndex);
		o2.setWorkingDistance(workingDistance);
	}

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// CorrectionCollar is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getObjectiveSettings() == null)
		{
			o1.setObjectiveSettings(new ObjectiveSettings());
		}
		ObjectiveSettings o2 = o1.getObjectiveSettings();
		o2.setCorrectionCollar(correctionCollar);
	}

	public void setObjectiveSettingsID(String id, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getObjectiveSettings() == null)
		{
			o1.setObjectiveSettings(new ObjectiveSettings());
		}
		ObjectiveSettings o2 = o1.getObjectiveSettings();
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	public void setObjectiveSettingsMedium(Medium medium, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Medium is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getObjectiveSettings() == null)
		{
			o1.setObjectiveSettings(new ObjectiveSettings());
		}
		ObjectiveSettings o2 = o1.getObjectiveSettings();
		o2.setMedium(medium);
	}

	// Ignoring ObjectiveRef back reference
	public void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// RefractiveIndex is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getObjectiveSettings() == null)
		{
			o1.setObjectiveSettings(new ObjectiveSettings());
		}
		ObjectiveSettings o2 = o1.getObjectiveSettings();
		o2.setRefractiveIndex(refractiveIndex);
	}

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setPixelsAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getImage(imageIndex).getPixels(),
				annotationLinks_reference);
	}

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// DimensionOrder is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setDimensionOrder(dimensionOrder);
	}

	public void setPixelsID(String id, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring MetadataOnly element, complex property
	public void setPixelsPhysicalSizeX(PositiveFloat physicalSizeX, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeX is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setPhysicalSizeX(physicalSizeX);
	}

	public void setPixelsPhysicalSizeY(PositiveFloat physicalSizeY, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeY is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setPhysicalSizeY(physicalSizeY);
	}

	public void setPixelsPhysicalSizeZ(PositiveFloat physicalSizeZ, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// PhysicalSizeZ is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setPhysicalSizeZ(physicalSizeZ);
	}

	// Ignoring Plane element, complex property
	public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeC is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setSizeC(sizeC);
	}

	public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeT is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setSizeT(sizeT);
	}

	public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeX is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setSizeX(sizeX);
	}

	public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeY is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setSizeY(sizeY);
	}

	public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// SizeZ is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setSizeZ(sizeZ);
	}

	// Ignoring TiffData element, complex property
	public void setPixelsTimeIncrement(Double timeIncrement, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// TimeIncrement is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setTimeIncrement(timeIncrement);
	}

	public void setPixelsType(PixelType type, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		o2.setType(type);
	}

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setPlaneAnnotationRef(String annotation, int imageIndex, int planeIndex, int annotationRefIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getImage(imageIndex).getPixels().getPlane(planeIndex),
				annotationLinks_reference);
	}

	public void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// DeltaT is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setDeltaT(deltaT);
	}

	public void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// ExposureTime is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setExposureTime(exposureTime);
	}

	public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// HashSHA1 is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setHashSHA1(hashSHA1);
	}

	// Ignoring Pixels_BackReference back reference
	public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionX is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setPositionX(positionX);
	}

	public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionY is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setPositionY(positionY);
	}

	public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PositionZ is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setPositionZ(positionZ);
	}

	public void setPlaneTheC(NonNegativeInteger theC, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheC is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setTheC(theC);
	}

	public void setPlaneTheT(NonNegativeInteger theT, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheT is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setTheT(theT);
	}

	public void setPlaneTheZ(NonNegativeInteger theZ, int imageIndex, int planeIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// TheZ is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfPlaneList() == planeIndex)
		{
			o2.addPlane(new Plane());
		}
		Plane o3 = o2.getPlane(planeIndex);
		o3.setTheZ(theZ);
	}

	//
	// Plate property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setPlateAnnotationRef(String annotation, int plateIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getPlate(plateIndex),
				annotationLinks_reference);
	}

	public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex)
	{
		// Parents: {u'OME': None}
		// ColumnNamingConvention is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setColumnNamingConvention(columnNamingConvention);
	}

	public void setPlateColumns(PositiveInteger columns, int plateIndex)
	{
		// Parents: {u'OME': None}
		// Columns is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setColumns(columns);
	}

	public void setPlateDescription(String description, int plateIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setDescription(description);
	}

	public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
	{
		// Parents: {u'OME': None}
		// ExternalIdentifier is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setExternalIdentifier(externalIdentifier);
	}

	public void setPlateFieldIndex(NonNegativeInteger fieldIndex, int plateIndex)
	{
		// Parents: {u'OME': None}
		// FieldIndex is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setFieldIndex(fieldIndex);
	}

	public void setPlateID(String id, int plateIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	public void setPlateName(String name, int plateIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setName(name);
	}

	// Ignoring PlateAcquisition element, complex property
	public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex)
	{
		// Parents: {u'OME': None}
		// RowNamingConvention is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setRowNamingConvention(rowNamingConvention);
	}

	public void setPlateRows(PositiveInteger rows, int plateIndex)
	{
		// Parents: {u'OME': None}
		// Rows is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setRows(rows);
	}

	// Ignoring Screen_BackReference back reference
	public void setPlateStatus(String status, int plateIndex)
	{
		// Parents: {u'OME': None}
		// Status is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setStatus(status);
	}

	// Ignoring Well element, complex property
	public void setPlateWellOriginX(Double wellOriginX, int plateIndex)
	{
		// Parents: {u'OME': None}
		// WellOriginX is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setWellOriginX(wellOriginX);
	}

	public void setPlateWellOriginY(Double wellOriginY, int plateIndex)
	{
		// Parents: {u'OME': None}
		// WellOriginY is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		o1.setWellOriginY(wellOriginY);
	}

	//
	// PlateAcquisition property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public void setPlateAcquisitionAnnotationRef(String annotation, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex),
				annotationLinks_reference);
	}

	public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfPlateAcquisitionList() == plateAcquisitionIndex)
		{
			o1.addPlateAcquisition(new PlateAcquisition());
		}
		PlateAcquisition o2 = o1.getPlateAcquisition(plateAcquisitionIndex);
		o2.setDescription(description);
	}

	public void setPlateAcquisitionEndTime(Timestamp endTime, int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// EndTime is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfPlateAcquisitionList() == plateAcquisitionIndex)
		{
			o1.addPlateAcquisition(new PlateAcquisition());
		}
		PlateAcquisition o2 = o1.getPlateAcquisition(plateAcquisitionIndex);
		o2.setEndTime(endTime);
	}

	public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfPlateAcquisitionList() == plateAcquisitionIndex)
		{
			o1.addPlateAcquisition(new PlateAcquisition());
		}
		PlateAcquisition o2 = o1.getPlateAcquisition(plateAcquisitionIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	public void setPlateAcquisitionMaximumFieldCount(PositiveInteger maximumFieldCount, int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// MaximumFieldCount is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfPlateAcquisitionList() == plateAcquisitionIndex)
		{
			o1.addPlateAcquisition(new PlateAcquisition());
		}
		PlateAcquisition o2 = o1.getPlateAcquisition(plateAcquisitionIndex);
		o2.setMaximumFieldCount(maximumFieldCount);
	}

	public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfPlateAcquisitionList() == plateAcquisitionIndex)
		{
			o1.addPlateAcquisition(new PlateAcquisition());
		}
		PlateAcquisition o2 = o1.getPlateAcquisition(plateAcquisitionIndex);
		o2.setName(name);
	}

	// Ignoring Plate_BackReference back reference
	public void setPlateAcquisitionStartTime(Timestamp startTime, int plateIndex, int plateAcquisitionIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// StartTime is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfPlateAcquisitionList() == plateAcquisitionIndex)
		{
			o1.addPlateAcquisition(new PlateAcquisition());
		}
		PlateAcquisition o2 = o1.getPlateAcquisition(plateAcquisitionIndex);
		o2.setStartTime(startTime);
	}

	public void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// WellSampleRef is reference and occurs more than once
		WellSampleRef wellSamples_reference = new WellSampleRef();
		wellSamples_reference.setID(wellSample);
		model.addReference(
				root.getPlate(plateIndex).getPlateAcquisition(plateAcquisitionIndex),
				wellSamples_reference);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setPointFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setPointFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setPointFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setPointFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setPointID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Point)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setPointLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setPointLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setPointStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setPointStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setPointText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setPointTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setPointTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setPointTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setPointTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setPointVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setVisible(visible);
	}

	public void setPointX(Double x, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setX(x);
	}

	public void setPointY(Double y, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Point());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Point)o3).setY(y);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setPolygonFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setPolygonFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setPolygonFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setPolygonFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setPolygonID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Polygon)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setPolygonLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setPolygonLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setPolygonStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolygonStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setPolygonStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setPolygonText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setPolygonTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setPolygonTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setPolygonTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setPolygonTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setPolygonVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setVisible(visible);
	}

	public void setPolygonPoints(String points, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polygon());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polygon)o3).setPoints(points);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setPolylineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setPolylineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setPolylineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setPolylineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setPolylineID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Polyline)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setPolylineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setPolylineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setPolylineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setPolylineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setPolylineText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setPolylineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setPolylineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setPolylineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setPolylineTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setPolylineVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setVisible(visible);
	}

	public void setPolylineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setMarkerEnd(markerEnd);
	}

	public void setPolylineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setMarkerStart(markerStart);
	}

	public void setPolylinePoints(String points, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Polyline());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Polyline)o3).setPoints(points);
	}

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setProjectAnnotationRef(String annotation, int projectIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getProject(projectIndex),
				annotationLinks_reference);
	}

	public void setProjectDatasetRef(String dataset, int projectIndex, int datasetRefIndex)
	{
		// Parents: {u'OME': None}
		// DatasetRef is reference and occurs more than once
		DatasetRef datasetLinks_reference = new DatasetRef();
		datasetLinks_reference.setID(dataset);
		model.addReference(
				root.getProject(projectIndex),
				datasetLinks_reference);
	}

	public void setProjectDescription(String description, int projectIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfProjectList() == projectIndex)
		{
			o0.addProject(new Project());
		}
		Project o1 = o0.getProject(projectIndex);
		o1.setDescription(description);
	}

	public void setProjectExperimenterGroupRef(String experimenterGroup, int projectIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterGroupRef is reference and occurs more than once
		ExperimenterGroupRef experimenterGroup_reference = new ExperimenterGroupRef();
		experimenterGroup_reference.setID(experimenterGroup);
		model.addReference(
				root.getProject(projectIndex),
				experimenterGroup_reference);
	}

	public void setProjectExperimenterRef(String experimenter, int projectIndex)
	{
		// Parents: {u'OME': None}
		// ExperimenterRef is reference and occurs more than once
		ExperimenterRef experimenter_reference = new ExperimenterRef();
		experimenter_reference.setID(experimenter);
		model.addReference(
				root.getProject(projectIndex),
				experimenter_reference);
	}

	public void setProjectID(String id, int projectIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfProjectList() == projectIndex)
		{
			o0.addProject(new Project());
		}
		Project o1 = o0.getProject(projectIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	public void setProjectName(String name, int projectIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfProjectList() == projectIndex)
		{
			o0.addProject(new Project());
		}
		Project o1 = o0.getProject(projectIndex);
		o1.setName(name);
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
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getROI(ROIIndex),
				annotationLinks_reference);
	}

	public void setROIDescription(String description, int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		o1.setDescription(description);
	}

	public void setROIID(String id, int ROIIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setROIName(String name, int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		o1.setName(name);
	}

	public void setROINamespace(String namespace, int ROIIndex)
	{
		// Parents: {u'OME': None}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		o1.setNamespace(namespace);
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
		// Parents: {u'Screen': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getScreen(screenIndex).getReagent(reagentIndex),
				annotationLinks_reference);
	}

	public void setReagentDescription(String description, int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		if (o1.sizeOfReagentList() == reagentIndex)
		{
			o1.addReagent(new Reagent());
		}
		Reagent o2 = o1.getReagent(reagentIndex);
		o2.setDescription(description);
	}

	public void setReagentID(String id, int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		if (o1.sizeOfReagentList() == reagentIndex)
		{
			o1.addReagent(new Reagent());
		}
		Reagent o2 = o1.getReagent(reagentIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	public void setReagentName(String name, int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		if (o1.sizeOfReagentList() == reagentIndex)
		{
			o1.addReagent(new Reagent());
		}
		Reagent o2 = o1.getReagent(reagentIndex);
		o2.setName(name);
	}

	public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex)
	{
		// Parents: {u'Screen': {u'OME': None}}
		// ReagentIdentifier is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		if (o1.sizeOfReagentList() == reagentIndex)
		{
			o1.addReagent(new Reagent());
		}
		Reagent o2 = o1.getReagent(reagentIndex);
		o2.setReagentIdentifier(reagentIdentifier);
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
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setFillColor(fillColor);
	}

	// FillRule accessor from parent Shape
	public void setRectangleFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setFillRule(fillRule);
	}

	// FontFamily accessor from parent Shape
	public void setRectangleFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setFontFamily(fontFamily);
	}

	// FontSize accessor from parent Shape
	public void setRectangleFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setFontSize(fontSize);
	}

	// FontStyle accessor from parent Shape
	public void setRectangleFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setFontStyle(fontStyle);
	}

	// ID accessor from parent Shape
	public void setRectangleID(String id, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		model.addModelObject(id, o3);
		((Rectangle)o3).setID(id);
	}

	// Ignoring Label of parent abstract type
	// Ignoring Line of parent abstract type
	// LineCap accessor from parent Shape
	public void setRectangleLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setLineCap(lineCap);
	}

	// Locked accessor from parent Shape
	public void setRectangleLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setLocked(locked);
	}

	// Ignoring Mask of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polygon of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// StrokeColor accessor from parent Shape
	public void setRectangleStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setStrokeColor(strokeColor);
	}

	// StrokeDashArray accessor from parent Shape
	public void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setStrokeDashArray(strokeDashArray);
	}

	// StrokeWidth accessor from parent Shape
	public void setRectangleStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setStrokeWidth(strokeWidth);
	}

	// Text accessor from parent Shape
	public void setRectangleText(String text, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setText(text);
	}

	// TheC accessor from parent Shape
	public void setRectangleTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setTheC(theC);
	}

	// TheT accessor from parent Shape
	public void setRectangleTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setTheT(theT);
	}

	// TheZ accessor from parent Shape
	public void setRectangleTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setTheZ(theZ);
	}

	// Transform accessor from parent Shape
	public void setRectangleTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setTransform(transform);
	}

	// Visible accessor from parent Shape
	public void setRectangleVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setVisible(visible);
	}

	public void setRectangleHeight(Double height, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setHeight(height);
	}

	public void setRectangleWidth(Double width, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setWidth(width);
	}

	public void setRectangleX(Double x, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setX(x);
	}

	public void setRectangleY(Double y, int ROIIndex, int shapeIndex)
	{
		// Parents: {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
		// Shape is abstract proprietary and not a reference
		OME o0 = root;
		if (o0.sizeOfROIList() == ROIIndex)
		{
			o0.addROI(new ROI());
		}
		ROI o1 = o0.getROI(ROIIndex);
		if (o1.getUnion() == null)
		{
			o1.setUnion(new Union());
		}
		Union o2 = o1.getUnion();
		if (o2.sizeOfShapeList() == shapeIndex)
		{
			o2.addShape(new Rectangle());
		}
		Shape o3 = o2.getShape(shapeIndex);
		((Rectangle)o3).setY(y);
	}

	//
	// Screen property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setScreenAnnotationRef(String annotation, int screenIndex, int annotationRefIndex)
	{
		// Parents: {u'OME': None}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getScreen(screenIndex),
				annotationLinks_reference);
	}

	public void setScreenDescription(String description, int screenIndex)
	{
		// Parents: {u'OME': None}
		// Description is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setDescription(description);
	}

	public void setScreenID(String id, int screenIndex)
	{
		// Parents: {u'OME': None}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		model.addModelObject(id, o1);
		o1.setID(id);
	}

	public void setScreenName(String name, int screenIndex)
	{
		// Parents: {u'OME': None}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setName(name);
	}

	public void setScreenPlateRef(String plate, int screenIndex, int plateRefIndex)
	{
		// Parents: {u'OME': None}
		// PlateRef is reference and occurs more than once
		PlateRef plateLinks_reference = new PlateRef();
		plateLinks_reference.setID(plate);
		model.addReference(
				root.getScreen(screenIndex),
				plateLinks_reference);
	}

	public void setScreenProtocolDescription(String protocolDescription, int screenIndex)
	{
		// Parents: {u'OME': None}
		// ProtocolDescription is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setProtocolDescription(protocolDescription);
	}

	public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex)
	{
		// Parents: {u'OME': None}
		// ProtocolIdentifier is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setProtocolIdentifier(protocolIdentifier);
	}

	// Ignoring Reagent element, complex property
	public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex)
	{
		// Parents: {u'OME': None}
		// ReagentSetDescription is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setReagentSetDescription(reagentSetDescription);
	}

	public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex)
	{
		// Parents: {u'OME': None}
		// ReagentSetIdentifier is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setReagentSetIdentifier(reagentSetIdentifier);
	}

	public void setScreenType(String type, int screenIndex)
	{
		// Parents: {u'OME': None}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfScreenList() == screenIndex)
		{
			o0.addScreen(new Screen());
		}
		Screen o1 = o0.getScreen(screenIndex);
		o1.setType(type);
	}

	//
	// StageLabel property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setStageLabelName(String name, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Name is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getStageLabel() == null)
		{
			o1.setStageLabel(new StageLabel());
		}
		StageLabel o2 = o1.getStageLabel();
		o2.setName(name);
	}

	public void setStageLabelX(Double x, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// X is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getStageLabel() == null)
		{
			o1.setStageLabel(new StageLabel());
		}
		StageLabel o2 = o1.getStageLabel();
		o2.setX(x);
	}

	public void setStageLabelY(Double y, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Y is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getStageLabel() == null)
		{
			o1.setStageLabel(new StageLabel());
		}
		StageLabel o2 = o1.getStageLabel();
		o2.setY(y);
	}

	public void setStageLabelZ(Double z, int imageIndex)
	{
		// Parents: {u'Image': {u'OME': None}}
		// Z is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getStageLabel() == null)
		{
			o1.setStageLabel(new StageLabel());
		}
		StageLabel o2 = o1.getStageLabel();
		o2.setZ(z);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getTagAnnotation(tagAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTagAnnotationDescription(String description, int tagAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTagAnnotationList() == tagAnnotationIndex)
		{
			o1.addTagAnnotation(new TagAnnotation());
		}
		TagAnnotation o2 = o1.getTagAnnotation(tagAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTagAnnotationID(String id, int tagAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTagAnnotationList() == tagAnnotationIndex)
		{
			o1.addTagAnnotation(new TagAnnotation());
		}
		TagAnnotation o2 = o1.getTagAnnotation(tagAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setTagAnnotationNamespace(String namespace, int tagAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTagAnnotationList() == tagAnnotationIndex)
		{
			o1.addTagAnnotation(new TagAnnotation());
		}
		TagAnnotation o2 = o1.getTagAnnotation(tagAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTagAnnotationList() == tagAnnotationIndex)
		{
			o1.addTagAnnotation(new TagAnnotation());
		}
		TagAnnotation o2 = o1.getTagAnnotation(tagAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getTermAnnotation(termAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTermAnnotationDescription(String description, int termAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTermAnnotationList() == termAnnotationIndex)
		{
			o1.addTermAnnotation(new TermAnnotation());
		}
		TermAnnotation o2 = o1.getTermAnnotation(termAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTermAnnotationID(String id, int termAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTermAnnotationList() == termAnnotationIndex)
		{
			o1.addTermAnnotation(new TermAnnotation());
		}
		TermAnnotation o2 = o1.getTermAnnotation(termAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setTermAnnotationNamespace(String namespace, int termAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTermAnnotationList() == termAnnotationIndex)
		{
			o1.addTermAnnotation(new TermAnnotation());
		}
		TermAnnotation o2 = o1.getTermAnnotation(termAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTermAnnotationList() == termAnnotationIndex)
		{
			o1.addTermAnnotation(new TermAnnotation());
		}
		TermAnnotation o2 = o1.getTermAnnotation(termAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstC is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		o3.setFirstC(firstC);
	}

	public void setTiffDataFirstT(NonNegativeInteger firstT, int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstT is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		o3.setFirstT(firstT);
	}

	public void setTiffDataFirstZ(NonNegativeInteger firstZ, int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// FirstZ is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		o3.setFirstZ(firstZ);
	}

	public void setTiffDataIFD(NonNegativeInteger ifd, int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// IFD is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		o3.setIFD(ifd);
	}

	// Ignoring Pixels_BackReference back reference
	public void setTiffDataPlaneCount(NonNegativeInteger planeCount, int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'Pixels': {u'Image': {u'OME': None}}}
		// PlaneCount is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		o3.setPlaneCount(planeCount);
	}

	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setTimestampAnnotationAnnotationRef(String annotation, int timestampAnnotationIndex, int annotationRefIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getTimestampAnnotation(timestampAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTimestampAnnotationDescription(String description, int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTimestampAnnotationList() == timestampAnnotationIndex)
		{
			o1.addTimestampAnnotation(new TimestampAnnotation());
		}
		TimestampAnnotation o2 = o1.getTimestampAnnotation(timestampAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setTimestampAnnotationID(String id, int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTimestampAnnotationList() == timestampAnnotationIndex)
		{
			o1.addTimestampAnnotation(new TimestampAnnotation());
		}
		TimestampAnnotation o2 = o1.getTimestampAnnotation(timestampAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTimestampAnnotationList() == timestampAnnotationIndex)
		{
			o1.addTimestampAnnotation(new TimestampAnnotation());
		}
		TimestampAnnotation o2 = o1.getTimestampAnnotation(timestampAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfTimestampAnnotationList() == timestampAnnotationIndex)
		{
			o1.addTimestampAnnotation(new TimestampAnnotation());
		}
		TimestampAnnotation o2 = o1.getTimestampAnnotation(timestampAnnotationIndex);
		o2.setValue(value);
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
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutIn is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		if (o2.getTransmittanceRange() == null)
		{
			o2.setTransmittanceRange(new TransmittanceRange());
		}
		TransmittanceRange o3 = o2.getTransmittanceRange();
		o3.setCutIn(cutIn);
	}

	public void setTransmittanceRangeCutInTolerance(NonNegativeInteger cutInTolerance, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutInTolerance is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		if (o2.getTransmittanceRange() == null)
		{
			o2.setTransmittanceRange(new TransmittanceRange());
		}
		TransmittanceRange o3 = o2.getTransmittanceRange();
		o3.setCutInTolerance(cutInTolerance);
	}

	public void setTransmittanceRangeCutOut(PositiveInteger cutOut, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutOut is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		if (o2.getTransmittanceRange() == null)
		{
			o2.setTransmittanceRange(new TransmittanceRange());
		}
		TransmittanceRange o3 = o2.getTransmittanceRange();
		o3.setCutOut(cutOut);
	}

	public void setTransmittanceRangeCutOutTolerance(NonNegativeInteger cutOutTolerance, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// CutOutTolerance is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		if (o2.getTransmittanceRange() == null)
		{
			o2.setTransmittanceRange(new TransmittanceRange());
		}
		TransmittanceRange o3 = o2.getTransmittanceRange();
		o3.setCutOutTolerance(cutOutTolerance);
	}

	public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex)
	{
		// Parents: {u'Filter': {u'Instrument': {u'OME': None}}}
		// Transmittance is not a reference
		OME o0 = root;
		if (o0.sizeOfInstrumentList() == instrumentIndex)
		{
			o0.addInstrument(new Instrument());
		}
		Instrument o1 = o0.getInstrument(instrumentIndex);
		if (o1.sizeOfFilterList() == filterIndex)
		{
			o1.addFilter(new Filter());
		}
		Filter o2 = o1.getFilter(filterIndex);
		if (o2.getTransmittanceRange() == null)
		{
			o2.setTransmittanceRange(new TransmittanceRange());
		}
		TransmittanceRange o3 = o2.getTransmittanceRange();
		o3.setTransmittance(transmittance);
	}

	//
	// UUID property storage
	//
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex)
	{
		// Parents: {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
		// FileName is not a reference
		OME o0 = root;
		if (o0.sizeOfImageList() == imageIndex)
		{
			o0.addImage(new Image());
		}
		Image o1 = o0.getImage(imageIndex);
		if (o1.getPixels() == null)
		{
			o1.setPixels(new Pixels());
		}
		Pixels o2 = o1.getPixels();
		if (o2.sizeOfTiffDataList() == tiffDataIndex)
		{
			o2.addTiffData(new TiffData());
		}
		TiffData o3 = o2.getTiffData(tiffDataIndex);
		if (o3.getUUID() == null)
		{
			o3.setUUID(new UUID());
		}
		UUID o4 = o3.getUUID();
		o4.setFileName(fileName);
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
		// Parents: {u'Plate': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getPlate(plateIndex).getWell(wellIndex),
				annotationLinks_reference);
	}

	public void setWellColor(Color color, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Color is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		o2.setColor(color);
	}

	public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Column is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		o2.setColumn(column);
	}

	public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ExternalDescription is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		o2.setExternalDescription(externalDescription);
	}

	public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ExternalIdentifier is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		o2.setExternalIdentifier(externalIdentifier);
	}

	public void setWellID(String id, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Plate_BackReference back reference
	public void setWellReagentRef(String reagent, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// ReagentRef is reference and occurs more than once
		ReagentRef reagent_reference = new ReagentRef();
		reagent_reference.setID(reagent);
		model.addReference(
				root.getPlate(plateIndex).getWell(wellIndex),
				reagent_reference);
	}

	public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Row is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		o2.setRow(row);
	}

	public void setWellType(String type, int plateIndex, int wellIndex)
	{
		// Parents: {u'Plate': {u'OME': None}}
		// Type is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		o2.setType(type);
	}

	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	public void setWellSampleAnnotationRef(String annotation, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex),
				annotationLinks_reference);
	}

	public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// ID is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		if (o2.sizeOfWellSampleList() == wellSampleIndex)
		{
			o2.addWellSample(new WellSample());
		}
		WellSample o3 = o2.getWellSample(wellSampleIndex);
		model.addModelObject(id, o3);
		o3.setID(id);
	}

	public void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// ImageRef is reference and occurs more than once
		ImageRef image_reference = new ImageRef();
		image_reference.setID(image);
		model.addReference(
				root.getPlate(plateIndex).getWell(wellIndex).getWellSample(wellSampleIndex),
				image_reference);
	}

	public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// Index is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		if (o2.sizeOfWellSampleList() == wellSampleIndex)
		{
			o2.addWellSample(new WellSample());
		}
		WellSample o3 = o2.getWellSample(wellSampleIndex);
		o3.setIndex(index);
	}

	// Ignoring PlateAcquisition_BackReference back reference
	public void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// PositionX is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		if (o2.sizeOfWellSampleList() == wellSampleIndex)
		{
			o2.addWellSample(new WellSample());
		}
		WellSample o3 = o2.getWellSample(wellSampleIndex);
		o3.setPositionX(positionX);
	}

	public void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// PositionY is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		if (o2.sizeOfWellSampleList() == wellSampleIndex)
		{
			o2.addWellSample(new WellSample());
		}
		WellSample o3 = o2.getWellSample(wellSampleIndex);
		o3.setPositionY(positionY);
	}

	public void setWellSampleTimepoint(Timestamp timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		// Parents: {u'Well': {u'Plate': {u'OME': None}}}
		// Timepoint is not a reference
		OME o0 = root;
		if (o0.sizeOfPlateList() == plateIndex)
		{
			o0.addPlate(new Plate());
		}
		Plate o1 = o0.getPlate(plateIndex);
		if (o1.sizeOfWellList() == wellIndex)
		{
			o1.addWell(new Well());
		}
		Well o2 = o1.getWell(wellIndex);
		if (o2.sizeOfWellSampleList() == wellSampleIndex)
		{
			o2.addWellSample(new WellSample());
		}
		WellSample o3 = o2.getWellSample(wellSampleIndex);
		o3.setTimepoint(timepoint);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// AnnotationRef is reference and occurs more than once
		AnnotationRef annotationLinks_reference = new AnnotationRef();
		annotationLinks_reference.setID(annotation);
		model.addReference(
				root.getStructuredAnnotations().getXMLAnnotation(XMLAnnotationIndex),
				annotationLinks_reference);
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setXMLAnnotationDescription(String description, int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Description is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfXMLAnnotationList() == XMLAnnotationIndex)
		{
			o1.addXMLAnnotation(new XMLAnnotation());
		}
		XMLAnnotation o2 = o1.getXMLAnnotation(XMLAnnotationIndex);
		o2.setDescription(description);
	}

	// Ignoring ExperimenterGroup_BackReference back reference
	// Ignoring Experimenter_BackReference back reference
	public void setXMLAnnotationID(String id, int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// ID is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfXMLAnnotationList() == XMLAnnotationIndex)
		{
			o1.addXMLAnnotation(new XMLAnnotation());
		}
		XMLAnnotation o2 = o1.getXMLAnnotation(XMLAnnotationIndex);
		model.addModelObject(id, o2);
		o2.setID(id);
	}

	// Ignoring Image_BackReference back reference
	public void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex)
	{
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Namespace is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfXMLAnnotationList() == XMLAnnotationIndex)
		{
			o1.addXMLAnnotation(new XMLAnnotation());
		}
		XMLAnnotation o2 = o1.getXMLAnnotation(XMLAnnotationIndex);
		o2.setNamespace(namespace);
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
		// Parents: {u'StructuredAnnotations': {u'OME': None}}
		// Value is not a reference
		OME o0 = root;
		if (o0.getStructuredAnnotations() == null)
		{
			o0.setStructuredAnnotations(new StructuredAnnotations());
		}
		StructuredAnnotations o1 = o0.getStructuredAnnotations();
		if (o1.sizeOfXMLAnnotationList() == XMLAnnotationIndex)
		{
			o1.addXMLAnnotation(new XMLAnnotation());
		}
		XMLAnnotation o2 = o1.getXMLAnnotation(XMLAnnotationIndex);
		o2.setValue(value);
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
