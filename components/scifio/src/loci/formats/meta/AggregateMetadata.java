/*
 * #%L
 * OME SCIFIO package for reading and converting scientific file formats.
 * %%
 * Copyright (C) 2005 - 2012 Board of Regents of the University of
 * Wisconsin-Madison, Glencoe Software, Inc., and University of Dundee.
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
 * Created by melissa via xsd-fu on 2012-01-12 20:06:11-0500
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import java.util.Iterator;
import java.util.List;

import ome.xml.model.enums.*;
import ome.xml.model.primitives.*;

/**
 * A metadata store which delegates the actual storage to one or more <i>sub</i>
 * metadata stores.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/meta/AggregateMetadata.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/meta/AggregateMetadata.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AggregateMetadata implements IMetadata
{
	// -- Fields --

	/** The active metadata store delegates. */
	private List delegates;

	// -- Constructor --

	/**
	 * Creates a new instance.
	 * @param delegates of type {@link MetadataRetrieve}
	 *   and/or {@link MetadataStore}.
	 */
	public AggregateMetadata(List delegates)
	{
		this.delegates = delegates;
	}

	// -- MetadataStore API methods --

	/* @see MetadataStore#createRoot() */
	public void createRoot()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				((MetadataStore) o).createRoot();
			}
		}
	}

	/**
	 * Unsupported with an AggregateMetadata.
	 * @throws RuntimeException Always.
	 */
	public Object getRoot()
	{
		throw new RuntimeException("Unsupported with AggregateMetadata. " +
		                           "Use getDelegates() and getRoot().");
	}

	/**
	 * Unsupported with an AggregateMetadata.
	 * @throws RuntimeException Always.
	 */
	public void setRoot(Object root)
	{
		throw new RuntimeException("Unsupported with AggregateMetadata. " +
	                               "Use getDelegates() and setRoot().");
	}


	// -- AggregateMetadata API methods --


	// -- Entity counting (manual definitions) --

	public int getPixelsBinDataCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPixelsBinDataCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public String getLightSourceType(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				return retrieve.getLightSourceType(instrumentIndex, lightSourceIndex);
			}
		}
		return null;
	}

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	public int getROIAnnotationRefCount(int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getROIAnnotationRefCount(ROIIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlateAcquisitionAnnotationRefCount(plateIndex, plateAcquisitionIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getPlateAnnotationRefCount(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlateAnnotationRefCount(plateIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getImageAnnotationRefCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getImageAnnotationRefCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getScreenAnnotationRefCount(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getScreenAnnotationRefCount(screenIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getWellAnnotationRefCount(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getWellAnnotationRefCount(plateIndex, wellIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getDatasetAnnotationRefCount(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDatasetAnnotationRefCount(datasetIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getProjectAnnotationRefCount(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getProjectAnnotationRefCount(projectIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getReagentAnnotationRefCount(screenIndex, reagentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlaneAnnotationRefCount(imageIndex, planeIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getExperimenterAnnotationRefCount(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimenterAnnotationRefCount(experimenterIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getWellSampleAnnotationRefCount(plateIndex, wellIndex, wellSampleIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getPixelsAnnotationRefCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPixelsAnnotationRefCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getChannelAnnotationRefCount(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getChannelAnnotationRefCount(imageIndex, channelIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Arc entity counting
	// BinaryFile entity counting
	// BinaryOnly entity counting
	// BooleanAnnotation entity counting
	public int getBooleanAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getBooleanAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Channel entity counting
	public int getChannelCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getChannelCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// CommentAnnotation entity counting
	public int getCommentAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getCommentAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Contact entity counting
	// Dataset entity counting
	public int getDatasetCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDatasetCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// DatasetRef entity counting
	public int getDatasetRefCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDatasetRefCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Detector entity counting
	public int getDetectorCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDetectorCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// DetectorSettings entity counting
	// Dichroic entity counting
	public int getDichroicCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDichroicCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// DichroicRef entity counting
	// DoubleAnnotation entity counting
	public int getDoubleAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDoubleAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Ellipse entity counting
	// EmissionFilterRef entity counting
	public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLightPathEmissionFilterRefCount(imageIndex, channelIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getFilterSetEmissionFilterRefCount(instrumentIndex, filterSetIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ExcitationFilterRef entity counting
	public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLightPathExcitationFilterRefCount(imageIndex, channelIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getFilterSetExcitationFilterRefCount(instrumentIndex, filterSetIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Experiment entity counting
	public int getExperimentCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimentCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ExperimentRef entity counting
	// Experimenter entity counting
	public int getExperimenterCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimenterCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ExperimenterRef entity counting
	// Filament entity counting
	// FileAnnotation entity counting
	public int getFileAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getFileAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Filter entity counting
	public int getFilterCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getFilterCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// FilterSet entity counting
	public int getFilterSetCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getFilterSetCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// FilterSetRef entity counting
	// Group entity counting
	public int getGroupCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getGroupCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// GroupRef entity counting
	public int getExperimenterGroupRefCount(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimenterGroupRefCount(experimenterIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Image entity counting
	public int getImageCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getImageCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ImageRef entity counting
	// ImagingEnvironment entity counting
	// Instrument entity counting
	public int getInstrumentCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getInstrumentCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// InstrumentRef entity counting
	// Laser entity counting
	// Leader entity counting
	// LightEmittingDiode entity counting
	// LightPath entity counting
	// LightSource entity counting
	public int getLightSourceCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLightSourceCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// LightSourceSettings entity counting
	public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getMicrobeamManipulationLightSourceSettingsCount(experimentIndex, microbeamManipulationIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Line entity counting
	// ListAnnotation entity counting
	public int getListAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getListAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// LongAnnotation entity counting
	public int getLongAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLongAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Mask entity counting
	// MetadataOnly entity counting
	// MicrobeamManipulation entity counting
	public int getMicrobeamManipulationCount(int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getMicrobeamManipulationCount(experimentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// MicrobeamManipulationRef entity counting
	public int getMicrobeamManipulationRefCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getMicrobeamManipulationRefCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Microscope entity counting
	// OTF entity counting
	public int getOTFCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getOTFCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// OTFRef entity counting
	// Objective entity counting
	public int getObjectiveCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getObjectiveCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ObjectiveSettings entity counting
	// Path entity counting
	// Pixels entity counting
	// Plane entity counting
	public int getPlaneCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlaneCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Plate entity counting
	public int getPlateCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlateCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// PlateAcquisition entity counting
	public int getPlateAcquisitionCount(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlateAcquisitionCount(plateIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// PlateRef entity counting
	public int getPlateRefCount(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getPlateRefCount(screenIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Point entity counting
	// Polyline entity counting
	// Project entity counting
	public int getProjectCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getProjectCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ProjectRef entity counting
	public int getProjectRefCount(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getProjectRefCount(datasetIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Pump entity counting
	// ROI entity counting
	public int getROICount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getROICount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ROIRef entity counting
	public int getImageROIRefCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getImageROIRefCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getMicrobeamManipulationROIRefCount(experimentIndex, microbeamManipulationIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Reagent entity counting
	public int getReagentCount(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getReagentCount(screenIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ReagentRef entity counting
	// Rectangle entity counting
	// Screen entity counting
	public int getScreenCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getScreenCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ScreenRef entity counting
	public int getScreenRefCount(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getScreenRefCount(plateIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Shape entity counting
	public int getShapeCount(int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getShapeCount(ROIIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// StageLabel entity counting
	// StructuredAnnotations entity counting
	// TagAnnotation entity counting
	public int getTagAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getTagAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// TermAnnotation entity counting
	public int getTermAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getTermAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// Text entity counting
	// TiffData entity counting
	public int getTiffDataCount(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getTiffDataCount(imageIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// TimestampAnnotation entity counting
	public int getTimestampAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getTimestampAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// TransmittanceRange entity counting
	// Element's text data
	// {u'TiffData': [u'int imageIndex', u'int tiffDataIndex']}
	public void setUUIDValue(String value, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				((MetadataStore) o).setUUIDValue(value, imageIndex, tiffDataIndex);
			}
		}
	}

	public String getUUIDValue(int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				String result = ((MetadataRetrieve) o).getUUIDValue(imageIndex, tiffDataIndex);
				if (result != null)
				{
					return result;
				}
			}
		}
		return null;
	}

	// UUID entity counting
	// Union entity counting
	// Well entity counting
	public int getWellCount(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getWellCount(plateIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// WellSample entity counting
	public int getWellSampleCount(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getWellSampleCount(plateIndex, wellIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// WellSampleRef entity counting
	public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getWellSampleRefCount(plateIndex, plateAcquisitionIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// XMLAnnotation entity counting
	public int getXMLAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getXMLAnnotationCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}


	// -- Entity retrieval (manual definitions) --

	public Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPixelsBinDataBigEndian(imageIndex, binDataIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// -- Entity retrieval (code generated definitions) --

	/** Gets the UUID associated with this collection of metadata. */
	public String getUUID()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getUUID();
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// AnnotationRef property storage
	//
	// Indexes: {u'ROI': [u'int ROIIndex', u'int annotationRefIndex'], u'Reagent': [u'int screenIndex', u'int reagentIndex', u'int annotationRefIndex'], u'Plate': [u'int plateIndex', u'int annotationRefIndex'], u'Image': [u'int imageIndex', u'int annotationRefIndex'], u'Well': [u'int plateIndex', u'int wellIndex', u'int annotationRefIndex'], u'Pixels': [u'int imageIndex', u'int annotationRefIndex'], u'Dataset': [u'int datasetIndex', u'int annotationRefIndex'], u'Project': [u'int projectIndex', u'int annotationRefIndex'], u'PlateAcquisition': [u'int plateIndex', u'int plateAcquisitionIndex', u'int annotationRefIndex'], u'Plane': [u'int imageIndex', u'int planeIndex', u'int annotationRefIndex'], u'Experimenter': [u'int experimenterIndex', u'int annotationRefIndex'], u'Annotation': [u'int annotationRefIndex'], u'WellSample': [u'int plateIndex', u'int wellIndex', u'int wellSampleIndex', u'int annotationRefIndex'], u'Screen': [u'int screenIndex', u'int annotationRefIndex'], u'Channel': [u'int imageIndex', u'int channelIndex', u'int annotationRefIndex']}
	// {u'ROI': {u'OME': None}, u'PlateAcquisition': {u'Plate': {u'OME': None}}, u'Plate': {u'OME': None}, u'Image': {u'OME': None}, u'Screen': {u'OME': None}, u'Well': {u'Plate': {u'OME': None}}, u'Dataset': {u'OME': None}, u'Project': {u'OME': None}, u'Reagent': {u'Screen': {u'OME': None}}, u'Plane': {u'Pixels': {u'Image': {u'OME': None}}}, u'Experimenter': {u'OME': None}, u'Annotation': None, u'WellSample': {u'Well': {u'Plate': {u'OME': None}}}, u'Pixels': {u'Image': {u'OME': None}}, u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getArcID(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getArcLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getArcLotNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getArcManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getArcManufacturer(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Model accessor from parent LightSource
	public String getArcModel(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getArcModel(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Power accessor from parent LightSource
	public Double getArcPower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getArcPower(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getArcSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getArcSerialNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public ArcType getArcType(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				ArcType result = retrieve.getArcType(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// BinaryFile property storage
	//
	// Indexes: {u'FileAnnotation': [u'int fileAnnotationIndex'], u'OTF': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	public String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationBinaryFileFileName(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getOTFBinaryFileFileName(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getOTFBinaryFileFileName(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationBinaryFileMIMEType(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getOTFBinaryFileMIMEType(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getOTFBinaryFileMIMEType(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeLong getFileAnnotationBinaryFileSize(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeLong result = retrieve.getFileAnnotationBinaryFileSize(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeLong getOTFBinaryFileSize(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeLong result = retrieve.getOTFBinaryFileSize(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBinaryOnlyMetadataFile(metadataFileIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getBinaryOnlyUUID(int UUIDIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBinaryOnlyUUID(UUIDIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBooleanAnnotationAnnotationRef(booleanAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getBooleanAnnotationDescription(int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBooleanAnnotationDescription(booleanAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getBooleanAnnotationID(int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBooleanAnnotationID(booleanAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getBooleanAnnotationNamespace(int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBooleanAnnotationNamespace(booleanAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public Boolean getBooleanAnnotationValue(int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getBooleanAnnotationValue(booleanAnnotationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AcquisitionMode result = retrieve.getChannelAcquisitionMode(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelAnnotationRef(imageIndex, channelIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Integer getChannelColor(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getChannelColor(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				ContrastMethod result = retrieve.getChannelContrastMethod(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring DetectorSettings element, complex property
	public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getChannelEmissionWavelength(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getChannelExcitationWavelength(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelFilterSetRef(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelFilterSetRef(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelFluor(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelFluor(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelID(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelID(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				IlluminationType result = retrieve.getChannelIlluminationType(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	public Double getChannelNDFilter(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getChannelNDFilter(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelName(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelName(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelOTFRef(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelOTFRef(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getChannelPinholeSize(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getChannelPinholeSize(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Integer getChannelPockelCellSetting(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getChannelPockelCellSetting(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getChannelSamplesPerPixel(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getChannelSamplesPerPixel(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getCommentAnnotationAnnotationRef(commentAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getCommentAnnotationDescription(int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getCommentAnnotationDescription(commentAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getCommentAnnotationID(int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getCommentAnnotationID(commentAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getCommentAnnotationNamespace(int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getCommentAnnotationNamespace(commentAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public String getCommentAnnotationValue(int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getCommentAnnotationValue(commentAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
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

	public String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetAnnotationRef(datasetIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDatasetDescription(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetDescription(datasetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDatasetExperimenterRef(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetExperimenterRef(datasetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDatasetGroupRef(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetGroupRef(datasetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDatasetID(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetID(datasetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getDatasetName(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetName(datasetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDatasetProjectRef(int datasetIndex, int projectRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetProjectRef(datasetIndex, projectRefIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorGain(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorGain(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDetectorID(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorID(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDetectorLotNumber(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorLotNumber(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDetectorManufacturer(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorManufacturer(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDetectorModel(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorModel(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorOffset(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorOffset(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorSerialNumber(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public DetectorType getDetectorType(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				DetectorType result = retrieve.getDetectorType(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorVoltage(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorVoltage(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorZoom(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorZoom(instrumentIndex, detectorIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Binning result = retrieve.getDetectorSettingsBinning(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorSettingsGain(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorSettingsGain(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDetectorSettingsID(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorSettingsID(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorSettingsOffset(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorSettingsOffset(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorSettingsReadOutRate(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorSettingsVoltage(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorSettingsVoltage(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDichroicID(instrumentIndex, dichroicIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring LightPath_BackReference back reference
	public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDichroicLotNumber(instrumentIndex, dichroicIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDichroicManufacturer(instrumentIndex, dichroicIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDichroicModel(int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDichroicModel(instrumentIndex, dichroicIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDichroicSerialNumber(instrumentIndex, dichroicIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDoubleAnnotationAnnotationRef(doubleAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getDoubleAnnotationDescription(int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDoubleAnnotationDescription(doubleAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getDoubleAnnotationID(int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDoubleAnnotationID(doubleAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getDoubleAnnotationNamespace(int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDoubleAnnotationNamespace(doubleAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public Double getDoubleAnnotationValue(int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDoubleAnnotationValue(doubleAnnotationIndex);
				if (result != null) return result;
			}
		}
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

	// Description accessor from parent Shape
	public String getEllipseDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getEllipseFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getEllipseFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getEllipseFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getEllipseFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getEllipseID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getEllipseLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getEllipseName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getEllipseStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getEllipseStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getEllipseStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getEllipseStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getEllipseStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getEllipseTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getEllipseTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getEllipseTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getEllipseTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getEllipseTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getEllipseTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getEllipseTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getEllipseRadiusX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getEllipseRadiusX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getEllipseRadiusY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getEllipseRadiusY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getEllipseX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getEllipseX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getEllipseY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getEllipseY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimentDescription(experimentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimentExperimenterRef(int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimentExperimenterRef(experimentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimentID(int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimentID(experimentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	public ExperimentType getExperimentType(int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				ExperimentType result = retrieve.getExperimentType(experimentIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterAnnotationRef(experimenterIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Dataset_BackReference back reference
	public String getExperimenterDisplayName(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterDisplayName(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterEmail(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterEmail(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experiment_BackReference back reference
	public String getExperimenterFirstName(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterFirstName(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupRef(experimenterIndex, groupRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterID(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterID(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getExperimenterInstitution(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterInstitution(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterLastName(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterLastName(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring MicrobeamManipulation_BackReference back reference
	public String getExperimenterMiddleName(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterMiddleName(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Project_BackReference back reference
	public String getExperimenterUserName(int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterUserName(experimenterIndex);
				if (result != null) return result;
			}
		}
		return null;
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilamentID(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getFilamentLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilamentLotNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getFilamentManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilamentManufacturer(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Model accessor from parent LightSource
	public String getFilamentModel(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilamentModel(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Power accessor from parent LightSource
	public Double getFilamentPower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getFilamentPower(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getFilamentSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilamentSerialNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public FilamentType getFilamentType(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FilamentType result = retrieve.getFilamentType(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationAnnotationRef(fileAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getFileAnnotationDescription(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationDescription(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getFileAnnotationID(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationID(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getFileAnnotationNamespace(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationNamespace(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Filter property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int filterIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public String getFilterFilterWheel(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterFilterWheel(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterID(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterID(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring LightPath_BackReference back reference
	public String getFilterLotNumber(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterLotNumber(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterManufacturer(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterManufacturer(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterModel(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterModel(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSerialNumber(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSerialNumber(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring TransmittanceRange element, complex property
	public FilterType getFilterType(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FilterType result = retrieve.getFilterType(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetDichroicRef(instrumentIndex, filterSetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetEmissionFilterRef(instrumentIndex, filterSetIndex, emissionFilterRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetExcitationFilterRef(instrumentIndex, filterSetIndex, excitationFilterRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSetID(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetID(instrumentIndex, filterSetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetLotNumber(instrumentIndex, filterSetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetManufacturer(instrumentIndex, filterSetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getFilterSetModel(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetModel(instrumentIndex, filterSetIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring OTF_BackReference back reference
	public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterSetSerialNumber(instrumentIndex, filterSetIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public String getGroupContact(int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGroupContact(groupIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Dataset_BackReference back reference
	public String getGroupDescription(int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGroupDescription(groupIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getGroupID(int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGroupID(groupIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getGroupLeader(int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGroupLeader(groupIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getGroupName(int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGroupName(groupIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public String getImageAcquiredDate(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageAcquiredDate(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageAnnotationRef(int imageIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageAnnotationRef(imageIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageDatasetRef(int imageIndex, int datasetRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageDatasetRef(imageIndex, datasetRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageDescription(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageDescription(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageExperimentRef(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageExperimentRef(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageExperimenterRef(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageExperimenterRef(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageGroupRef(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageGroupRef(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageID(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageID(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring ImagingEnvironment element, complex property
	public String getImageInstrumentRef(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageInstrumentRef(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageMicrobeamManipulationRef(imageIndex, microbeamManipulationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageName(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageName(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	public String getImageROIRef(int imageIndex, int ROIRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageROIRef(imageIndex, ROIRefIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public Double getImagingEnvironmentAirPressure(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getImagingEnvironmentAirPressure(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PercentFraction result = retrieve.getImagingEnvironmentCO2Percent(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PercentFraction getImagingEnvironmentHumidity(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PercentFraction result = retrieve.getImagingEnvironmentHumidity(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getImagingEnvironmentTemperature(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getImagingEnvironmentTemperature(imageIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getInstrumentID(instrumentIndex);
				if (result != null) return result;
			}
		}
		return null;
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserID(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLaserLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserLotNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getLaserManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserManufacturer(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Model accessor from parent LightSource
	public String getLaserModel(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserModel(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Power accessor from parent LightSource
	public Double getLaserPower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLaserPower(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getLaserSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserSerialNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public LaserMedium getLaserLaserMedium(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LaserMedium result = retrieve.getLaserLaserMedium(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getLaserPockelCell(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Pulse getLaserPulse(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Pulse result = retrieve.getLaserPulse(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getLaserPump(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserPump(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLaserRepetitionRate(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getLaserTuneable(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public LaserType getLaserType(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LaserType result = retrieve.getLaserType(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getLaserWavelength(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getLaserWavelength(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightEmittingDiodeID(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightEmittingDiodeLotNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightEmittingDiodeManufacturer(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Model accessor from parent LightSource
	public String getLightEmittingDiodeModel(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightEmittingDiodeModel(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Power accessor from parent LightSource
	public Double getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLightEmittingDiodePower(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightEmittingDiodeSerialNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightPathDichroicRef(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightPathEmissionFilterRef(imageIndex, channelIndex, emissionFilterRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightPathExcitationFilterRef(imageIndex, channelIndex, excitationFilterRefIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PercentFraction result = retrieve.getChannelLightSourceSettingsAttenuation(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PercentFraction result = retrieve.getMicrobeamManipulationLightSourceSettingsAttenuation(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getChannelLightSourceSettingsID(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicrobeamManipulationLightSourceSettingsID(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getChannelLightSourceSettingsWavelength(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getMicrobeamManipulationLightSourceSettingsWavelength(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// Line property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public String getLineDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getLineFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getLineFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getLineFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLineFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getLineID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getLineLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getLineName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getLineStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getLineStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getLineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getLineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLineStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getLineTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLineTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getLineTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLineTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getLineTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLineTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getLineTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLineX1(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLineX1(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLineX2(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLineX2(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLineY1(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLineY1(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLineY2(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLineY2(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getListAnnotationAnnotationRef(listAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getListAnnotationDescription(int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getListAnnotationDescription(listAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getListAnnotationID(int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getListAnnotationID(listAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getListAnnotationNamespace(int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getListAnnotationNamespace(listAnnotationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLongAnnotationAnnotationRef(longAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getLongAnnotationDescription(int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLongAnnotationDescription(longAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getLongAnnotationID(int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLongAnnotationID(longAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getLongAnnotationNamespace(int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLongAnnotationNamespace(longAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public Long getLongAnnotationValue(int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Long result = retrieve.getLongAnnotationValue(longAnnotationIndex);
				if (result != null) return result;
			}
		}
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

	// Description accessor from parent Shape
	public String getMaskDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getMaskFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getMaskFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getMaskFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getMaskFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getMaskID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getMaskLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getMaskName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getMaskStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getMaskStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getMaskStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getMaskStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getMaskStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getMaskTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getMaskTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getMaskTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getMaskTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getMaskTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getMaskTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getMaskTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring BinData element, complex property
	public Double getMaskHeight(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getMaskHeight(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getMaskWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getMaskWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getMaskX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getMaskX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getMaskY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getMaskY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicrobeamManipulationDescription(experimentIndex, microbeamManipulationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicrobeamManipulationExperimenterRef(experimentIndex, microbeamManipulationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicrobeamManipulationID(experimentIndex, microbeamManipulationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicrobeamManipulationROIRef(experimentIndex, microbeamManipulationIndex, ROIRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				MicrobeamManipulationType result = retrieve.getMicrobeamManipulationType(experimentIndex, microbeamManipulationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicroscopeLotNumber(instrumentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMicroscopeManufacturer(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicroscopeManufacturer(instrumentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMicroscopeModel(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicroscopeModel(instrumentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMicroscopeSerialNumber(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMicroscopeSerialNumber(instrumentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public MicroscopeType getMicroscopeType(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				MicroscopeType result = retrieve.getMicroscopeType(instrumentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// OTF property storage
	//
	// Indexes: {u'Instrument': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	public String getOTFFilterSetRef(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getOTFFilterSetRef(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getOTFID(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getOTFID(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring ObjectiveSettings element, complex property
	public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getOTFOpticalAxisAveraged(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getOTFSizeX(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getOTFSizeX(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getOTFSizeY(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getOTFSizeY(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PixelType getOTFType(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PixelType result = retrieve.getOTFType(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Correction result = retrieve.getObjectiveCorrection(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getObjectiveID(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveID(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Immersion result = retrieve.getObjectiveImmersion(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getObjectiveIris(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getObjectiveLensNA(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveLotNumber(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getObjectiveModel(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveModel(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// ObjectiveSettings property storage
	//
	// Indexes: {u'Image': [u'int imageIndex'], u'OTF': [u'int instrumentIndex', u'int OTFIndex']}
	// {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	public Double getImageObjectiveSettingsCorrectionCollar(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getImageObjectiveSettingsCorrectionCollar(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getOTFObjectiveSettingsCorrectionCollar(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getImageObjectiveSettingsID(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageObjectiveSettingsID(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getOTFObjectiveSettingsID(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getOTFObjectiveSettingsID(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Medium getImageObjectiveSettingsMedium(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Medium result = retrieve.getImageObjectiveSettingsMedium(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Medium result = retrieve.getOTFObjectiveSettingsMedium(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getImageObjectiveSettingsRefractiveIndex(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getImageObjectiveSettingsRefractiveIndex(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getOTFObjectiveSettingsRefractiveIndex(instrumentIndex, OTFIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// Path property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public String getPathDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getPathFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getPathFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getPathFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPathFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getPathID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getPathLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getPathName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getPathStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getPathStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPathStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getPathStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPathStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getPathTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPathTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPathTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPathTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPathTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPathTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getPathTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPathDefinition(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPathDefinition(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPixelsAnnotationRef(imageIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	public DimensionOrder getPixelsDimensionOrder(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				DimensionOrder result = retrieve.getPixelsDimensionOrder(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPixelsID(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPixelsID(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring MetadataOnly element, complex property
	public PositiveFloat getPixelsPhysicalSizeX(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveFloat result = retrieve.getPixelsPhysicalSizeX(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveFloat getPixelsPhysicalSizeY(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveFloat result = retrieve.getPixelsPhysicalSizeY(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveFloat getPixelsPhysicalSizeZ(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveFloat result = retrieve.getPixelsPhysicalSizeZ(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Plane element, complex property
	public PositiveInteger getPixelsSizeC(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPixelsSizeC(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPixelsSizeT(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPixelsSizeT(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPixelsSizeX(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPixelsSizeX(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPixelsSizeY(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPixelsSizeY(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPixelsSizeZ(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPixelsSizeZ(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring TiffData element, complex property
	public Double getPixelsTimeIncrement(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPixelsTimeIncrement(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PixelType getPixelsType(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PixelType result = retrieve.getPixelsType(imageIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlaneAnnotationRef(imageIndex, planeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPlaneDeltaT(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlaneDeltaT(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPlaneExposureTime(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlaneExposureTime(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlaneHashSHA1(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlaneHashSHA1(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPlanePositionX(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlanePositionX(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPlanePositionY(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlanePositionY(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPlanePositionZ(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlanePositionZ(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getPlaneTheC(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPlaneTheC(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getPlaneTheT(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPlaneTheT(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getPlaneTheZ(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPlaneTheZ(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAnnotationRef(plateIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NamingConvention getPlateColumnNamingConvention(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NamingConvention result = retrieve.getPlateColumnNamingConvention(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPlateColumns(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPlateColumns(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateDescription(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateDescription(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateExternalIdentifier(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateExternalIdentifier(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateID(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateID(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateName(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateName(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring PlateAcquisition element, complex property
	public NamingConvention getPlateRowNamingConvention(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NamingConvention result = retrieve.getPlateRowNamingConvention(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPlateRows(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPlateRows(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateScreenRef(int plateIndex, int screenRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateScreenRef(plateIndex, screenRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateStatus(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateStatus(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Well element, complex property
	public Double getPlateWellOriginX(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlateWellOriginX(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPlateWellOriginY(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPlateWellOriginY(plateIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionAnnotationRef(plateIndex, plateAcquisitionIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionDescription(plateIndex, plateAcquisitionIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionEndTime(plateIndex, plateAcquisitionIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionID(plateIndex, plateAcquisitionIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPlateAcquisitionMaximumFieldCount(plateIndex, plateAcquisitionIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionName(plateIndex, plateAcquisitionIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionStartTime(plateIndex, plateAcquisitionIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPlateAcquisitionWellSampleRef(plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
				if (result != null) return result;
			}
		}
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

	// Description accessor from parent Shape
	public String getPointDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getPointFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getPointFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getPointFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPointFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getPointID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getPointLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getPointName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getPointStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getPointStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPointStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getPointStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPointStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getPointTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPointTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPointTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPointTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPointTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPointTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getPointTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPointX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPointX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getPointY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPointY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// Polyline property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public String getPolylineDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getPolylineFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getPolylineFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getPolylineFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolylineFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getPolylineID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getPolylineLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getPolylineName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getPolylineStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getPolylineStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPolylineStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getPolylineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getPolylineStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getPolylineTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolylineTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPolylineTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolylineTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPolylineTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolylineTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getPolylineTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Boolean getPolylineClosed(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPolylineClosed(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPolylinePoints(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylinePoints(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectAnnotationRef(projectIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Dataset_BackReference back reference
	public String getProjectDescription(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectDescription(projectIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getProjectExperimenterRef(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectExperimenterRef(projectIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getProjectGroupRef(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectGroupRef(projectIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getProjectID(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectID(projectIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getProjectName(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectName(projectIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public String getROIAnnotationRef(int ROIIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getROIAnnotationRef(ROIIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getROIDescription(int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getROIDescription(ROIIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getROIID(int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getROIID(ROIIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public String getROIName(int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getROIName(ROIIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getROINamespace(int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getROINamespace(ROIIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getReagentAnnotationRef(screenIndex, reagentIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getReagentDescription(int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getReagentDescription(screenIndex, reagentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getReagentID(int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getReagentID(screenIndex, reagentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getReagentName(int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getReagentName(screenIndex, reagentIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getReagentReagentIdentifier(int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getReagentReagentIdentifier(screenIndex, reagentIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	// Description accessor from parent Shape
	public String getRectangleDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getRectangleFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getRectangleFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getRectangleFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getRectangleFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getRectangleID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getRectangleLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getRectangleName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getRectangleStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getRectangleStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getRectangleStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getRectangleStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getRectangleStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getRectangleTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getRectangleTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getRectangleTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getRectangleTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getRectangleTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getRectangleTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getRectangleTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getRectangleHeight(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getRectangleHeight(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getRectangleWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getRectangleWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getRectangleX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getRectangleX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getRectangleY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getRectangleY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenAnnotationRef(screenIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenDescription(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenDescription(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenID(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenID(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenName(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenName(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenPlateRef(int screenIndex, int plateRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenPlateRef(screenIndex, plateRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenProtocolDescription(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenProtocolDescription(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenProtocolIdentifier(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenProtocolIdentifier(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Reagent element, complex property
	public String getScreenReagentSetDescription(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenReagentSetDescription(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenReagentSetIdentifier(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenReagentSetIdentifier(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getScreenType(int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getScreenType(screenIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public String getStageLabelName(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getStageLabelName(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getStageLabelX(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getStageLabelX(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getStageLabelY(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getStageLabelY(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getStageLabelZ(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getStageLabelZ(imageIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTagAnnotationAnnotationRef(tagAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTagAnnotationDescription(int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTagAnnotationDescription(tagAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getTagAnnotationID(int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTagAnnotationID(tagAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getTagAnnotationNamespace(int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTagAnnotationNamespace(tagAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public String getTagAnnotationValue(int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTagAnnotationValue(tagAnnotationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTermAnnotationAnnotationRef(termAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTermAnnotationDescription(int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTermAnnotationDescription(termAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getTermAnnotationID(int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTermAnnotationID(termAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getTermAnnotationNamespace(int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTermAnnotationNamespace(termAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public String getTermAnnotationValue(int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTermAnnotationValue(termAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Text property storage
	//
	// Indexes: {u'Shape': [u'int ROIIndex', u'int shapeIndex']}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public String getTextDescription(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextDescription(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public Integer getTextFill(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getTextFill(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public NonNegativeInteger getTextFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTextFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public String getTextID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Label accessor from parent Shape
	public String getTextLabel(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextLabel(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public String getTextName(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextName(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public Integer getTextStroke(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getTextStroke(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getTextStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Double getTextStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getTextStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public NonNegativeInteger getTextTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTextTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getTextTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTextTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getTextTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTextTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public String getTextTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getTextValue(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTextValue(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getTextX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getTextX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getTextY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getTextY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	//
	// TiffData property storage
	//
	// Indexes: {u'Pixels': [u'int imageIndex', u'int tiffDataIndex']}
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public NonNegativeInteger getTiffDataFirstC(int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTiffDataFirstC(imageIndex, tiffDataIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getTiffDataFirstT(int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTiffDataFirstT(imageIndex, tiffDataIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getTiffDataFirstZ(int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTiffDataFirstZ(imageIndex, tiffDataIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getTiffDataIFD(int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTiffDataIFD(imageIndex, tiffDataIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getTiffDataPlaneCount(int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTiffDataPlaneCount(imageIndex, tiffDataIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTimestampAnnotationAnnotationRef(timestampAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getTimestampAnnotationDescription(int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTimestampAnnotationDescription(timestampAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getTimestampAnnotationID(int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTimestampAnnotationID(timestampAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getTimestampAnnotationNamespace(int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTimestampAnnotationNamespace(timestampAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public String getTimestampAnnotationValue(int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTimestampAnnotationValue(timestampAnnotationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PercentFraction result = retrieve.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getUUIDFileName(imageIndex, tiffDataIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellAnnotationRef(plateIndex, wellIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Integer getWellColor(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Integer result = retrieve.getWellColor(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getWellColumn(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellExternalDescription(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellExternalDescription(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellExternalIdentifier(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellExternalIdentifier(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellID(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellID(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellReagentRef(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellReagentRef(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getWellRow(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getWellRow(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellStatus(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellStatus(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellSampleAnnotationRef(plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring PlateAcquisition_BackReference back reference
	public Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getWellSamplePositionX(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getWellSamplePositionY(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
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

	public String getXMLAnnotationAnnotationRef(int XMLAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getXMLAnnotationAnnotationRef(XMLAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public String getXMLAnnotationDescription(int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getXMLAnnotationDescription(XMLAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Experimenter_BackReference back reference
	public String getXMLAnnotationID(int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getXMLAnnotationID(XMLAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring Image_BackReference back reference
	public String getXMLAnnotationNamespace(int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getXMLAnnotationNamespace(XMLAnnotationIndex);
				if (result != null) return result;
			}
		}
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
	public String getXMLAnnotationValue(int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getXMLAnnotationValue(XMLAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference

	// -- Entity storage (manual definitions) --

	public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsBinDataBigEndian(bigEndian, imageIndex, binDataIndex);
			}
		}
	}

	public void setMaskBinData(byte[] binData, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskBinData(binData, ROIIndex, shapeIndex);
			}
		}
	}

	// -- Entity storage (code generated definitions) --

	/** Sets the UUID associated with this collection of metadata. */
	public void setUUID(String uuid)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				((MetadataStore) o).setUUID(uuid);
			}
		}
	}

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
	public void setArcID(String id, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcID(id, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setArcLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcLotNumber(lotNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Manufacturer accessor from parent LightSource
	public void setArcManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Model accessor from parent LightSource
	public void setArcModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcModel(model, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Power accessor from parent LightSource
	public void setArcPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcPower(power, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// SerialNumber accessor from parent LightSource
	public void setArcSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setArcType(ArcType type, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcType(type, instrumentIndex, lightSourceIndex);
			}
		}
	}

	//
	// BinaryFile property storage
	//
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	// Ignoring BinData element, complex property
	// Ignoring External element, complex property
	public void setFileAnnotationBinaryFileFileName(String fileName, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationBinaryFileFileName(fileName, fileAnnotationIndex);
			}
		}
	}

	public void setOTFBinaryFileFileName(String fileName, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFBinaryFileFileName(fileName, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setFileAnnotationBinaryFileMIMEType(String mimetype, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationBinaryFileMIMEType(mimetype, fileAnnotationIndex);
			}
		}
	}

	public void setOTFBinaryFileMIMEType(String mimetype, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFBinaryFileMIMEType(mimetype, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setFileAnnotationBinaryFileSize(NonNegativeLong size, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationBinaryFileSize(size, fileAnnotationIndex);
			}
		}
	}

	public void setOTFBinaryFileSize(NonNegativeLong size, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFBinaryFileSize(size, instrumentIndex, OTFIndex);
			}
		}
	}

	//
	// BinaryOnly property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setBinaryOnlyMetadataFile(String metadataFile)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBinaryOnlyMetadataFile(metadataFile);
			}
		}
	}

	public void setBinaryOnlyUUID(String uuid)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBinaryOnlyUUID(uuid);
			}
		}
	}

	//
	// BooleanAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setBooleanAnnotationAnnotationRef(String annotation, int booleanAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBooleanAnnotationAnnotationRef(annotation, booleanAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setBooleanAnnotationDescription(String description, int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBooleanAnnotationDescription(description, booleanAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setBooleanAnnotationID(String id, int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBooleanAnnotationID(id, booleanAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBooleanAnnotationNamespace(namespace, booleanAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBooleanAnnotationValue(value, booleanAnnotationIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelAcquisitionMode(acquisitionMode, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelAnnotationRef(annotation, imageIndex, channelIndex, annotationRefIndex);
			}
		}
	}

	public void setChannelColor(Integer color, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelColor(color, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelContrastMethod(contrastMethod, imageIndex, channelIndex);
			}
		}
	}

	// Ignoring DetectorSettings element, complex property
	public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelEmissionWavelength(emissionWavelength, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelExcitationWavelength(excitationWavelength, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelFilterSetRef(String filterSet, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelFilterSetRef(filterSet, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelFluor(String fluor, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelFluor(fluor, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelID(String id, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelID(id, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelIlluminationType(illuminationType, imageIndex, channelIndex);
			}
		}
	}

	// Ignoring LightPath element, complex property
	// Ignoring LightSourceSettings element, complex property
	public void setChannelNDFilter(Double ndfilter, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelNDFilter(ndfilter, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelName(String name, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelName(name, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelOTFRef(String otf, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelOTFRef(otf, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelPinholeSize(pinholeSize, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelPockelCellSetting(pockelCellSetting, imageIndex, channelIndex);
			}
		}
	}

	public void setChannelSamplesPerPixel(PositiveInteger samplesPerPixel, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelSamplesPerPixel(samplesPerPixel, imageIndex, channelIndex);
			}
		}
	}

	//
	// CommentAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setCommentAnnotationAnnotationRef(String annotation, int commentAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setCommentAnnotationAnnotationRef(annotation, commentAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setCommentAnnotationDescription(String description, int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setCommentAnnotationDescription(description, commentAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setCommentAnnotationID(String id, int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setCommentAnnotationID(id, commentAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setCommentAnnotationNamespace(String namespace, int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setCommentAnnotationNamespace(namespace, commentAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setCommentAnnotationValue(String value, int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setCommentAnnotationValue(value, commentAnnotationIndex);
			}
		}
	}

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

	public void setDatasetAnnotationRef(String annotation, int datasetIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetAnnotationRef(annotation, datasetIndex, annotationRefIndex);
			}
		}
	}

	public void setDatasetDescription(String description, int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetDescription(description, datasetIndex);
			}
		}
	}

	public void setDatasetExperimenterRef(String experimenter, int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetExperimenterRef(experimenter, datasetIndex);
			}
		}
	}

	public void setDatasetGroupRef(String group, int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetGroupRef(group, datasetIndex);
			}
		}
	}

	public void setDatasetID(String id, int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetID(id, datasetIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setDatasetName(String name, int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetName(name, datasetIndex);
			}
		}
	}

	public void setDatasetProjectRef(String project, int datasetIndex, int projectRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetProjectRef(project, datasetIndex, projectRefIndex);
			}
		}
	}

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

	public void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorGain(gain, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorID(String id, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorID(id, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorLotNumber(lotNumber, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorModel(String model, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorModel(model, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorOffset(offset, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorType(type, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
			}
		}
	}

	public void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
			}
		}
	}

	//
	// DetectorSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsBinning(binning, imageIndex, channelIndex);
			}
		}
	}

	public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsGain(gain, imageIndex, channelIndex);
			}
		}
	}

	public void setDetectorSettingsID(String id, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsID(id, imageIndex, channelIndex);
			}
		}
	}

	public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsOffset(offset, imageIndex, channelIndex);
			}
		}
	}

	public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsReadOutRate(readOutRate, imageIndex, channelIndex);
			}
		}
	}

	public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsVoltage(voltage, imageIndex, channelIndex);
			}
		}
	}

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring FilterSet_BackReference back reference
	public void setDichroicID(String id, int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDichroicID(id, instrumentIndex, dichroicIndex);
			}
		}
	}

	// Ignoring LightPath_BackReference back reference
	public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex);
			}
		}
	}

	public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex);
			}
		}
	}

	public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDichroicModel(model, instrumentIndex, dichroicIndex);
			}
		}
	}

	public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDichroicSerialNumber(serialNumber, instrumentIndex, dichroicIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDoubleAnnotationAnnotationRef(annotation, doubleAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setDoubleAnnotationDescription(String description, int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDoubleAnnotationDescription(description, doubleAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setDoubleAnnotationID(String id, int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDoubleAnnotationID(id, doubleAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDoubleAnnotationNamespace(namespace, doubleAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDoubleAnnotationValue(value, doubleAnnotationIndex);
			}
		}
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public void setEllipseDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setEllipseFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setEllipseFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setEllipseID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setEllipseLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setEllipseName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setEllipseStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setEllipseStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setEllipseStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setEllipseTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setEllipseTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setEllipseTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setEllipseTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setEllipseRadiusX(Double radiusX, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseRadiusX(radiusX, ROIIndex, shapeIndex);
			}
		}
	}

	public void setEllipseRadiusY(Double radiusY, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseRadiusY(radiusY, ROIIndex, shapeIndex);
			}
		}
	}

	public void setEllipseX(Double x, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseX(x, ROIIndex, shapeIndex);
			}
		}
	}

	public void setEllipseY(Double y, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseY(y, ROIIndex, shapeIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimentDescription(description, experimentIndex);
			}
		}
	}

	public void setExperimentExperimenterRef(String experimenter, int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimentExperimenterRef(experimenter, experimentIndex);
			}
		}
	}

	public void setExperimentID(String id, int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimentID(id, experimentIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation element, complex property
	public void setExperimentType(ExperimentType type, int experimentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimentType(type, experimentIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterAnnotationRef(annotation, experimenterIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Dataset_BackReference back reference
	public void setExperimenterDisplayName(String displayName, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterDisplayName(displayName, experimenterIndex);
			}
		}
	}

	public void setExperimenterEmail(String email, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterEmail(email, experimenterIndex);
			}
		}
	}

	// Ignoring Experiment_BackReference back reference
	public void setExperimenterFirstName(String firstName, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterFirstName(firstName, experimenterIndex);
			}
		}
	}

	public void setExperimenterGroupRef(String group, int experimenterIndex, int groupRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupRef(group, experimenterIndex, groupRefIndex);
			}
		}
	}

	public void setExperimenterID(String id, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterID(id, experimenterIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setExperimenterInstitution(String institution, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterInstitution(institution, experimenterIndex);
			}
		}
	}

	public void setExperimenterLastName(String lastName, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterLastName(lastName, experimenterIndex);
			}
		}
	}

	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setExperimenterMiddleName(String middleName, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterMiddleName(middleName, experimenterIndex);
			}
		}
	}

	// Ignoring Project_BackReference back reference
	public void setExperimenterUserName(String userName, int experimenterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterUserName(userName, experimenterIndex);
			}
		}
	}

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
	public void setFilamentID(String id, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentID(id, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentLotNumber(lotNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Manufacturer accessor from parent LightSource
	public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Model accessor from parent LightSource
	public void setFilamentModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentModel(model, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Power accessor from parent LightSource
	public void setFilamentPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentPower(power, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// SerialNumber accessor from parent LightSource
	public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setFilamentType(FilamentType type, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentType(type, instrumentIndex, lightSourceIndex);
			}
		}
	}

	//
	// FileAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setFileAnnotationAnnotationRef(String annotation, int fileAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationAnnotationRef(annotation, fileAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setFileAnnotationDescription(String description, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationDescription(description, fileAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setFileAnnotationID(String id, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationID(id, fileAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationNamespace(namespace, fileAnnotationIndex);
			}
		}
	}

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
	public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex);
			}
		}
	}

	public void setFilterID(String id, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterID(id, instrumentIndex, filterIndex);
			}
		}
	}

	// Ignoring LightPath_BackReference back reference
	public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
			}
		}
	}

	public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
			}
		}
	}

	public void setFilterModel(String model, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterModel(model, instrumentIndex, filterIndex);
			}
		}
	}

	public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSerialNumber(serialNumber, instrumentIndex, filterIndex);
			}
		}
	}

	// Ignoring TransmittanceRange element, complex property
	public void setFilterType(FilterType type, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterType(type, instrumentIndex, filterIndex);
			}
		}
	}

	//
	// FilterSet property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring Channel_BackReference back reference
	public void setFilterSetDichroicRef(String dichroic, int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetDichroicRef(dichroic, instrumentIndex, filterSetIndex);
			}
		}
	}

	public void setFilterSetEmissionFilterRef(String emissionFilter, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetEmissionFilterRef(emissionFilter, instrumentIndex, filterSetIndex, emissionFilterRefIndex);
			}
		}
	}

	public void setFilterSetExcitationFilterRef(String excitationFilter, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetExcitationFilterRef(excitationFilter, instrumentIndex, filterSetIndex, excitationFilterRefIndex);
			}
		}
	}

	public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetID(id, instrumentIndex, filterSetIndex);
			}
		}
	}

	public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex);
			}
		}
	}

	public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex);
			}
		}
	}

	public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetModel(model, instrumentIndex, filterSetIndex);
			}
		}
	}

	// Ignoring OTF_BackReference back reference
	public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterSetSerialNumber(serialNumber, instrumentIndex, filterSetIndex);
			}
		}
	}

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

	public void setGroupContact(String contact, int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGroupContact(contact, groupIndex);
			}
		}
	}

	// Ignoring Dataset_BackReference back reference
	public void setGroupDescription(String description, int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGroupDescription(description, groupIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setGroupID(String id, int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGroupID(id, groupIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setGroupLeader(String leader, int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGroupLeader(leader, groupIndex);
			}
		}
	}

	public void setGroupName(String name, int groupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGroupName(name, groupIndex);
			}
		}
	}

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

	public void setImageAcquiredDate(String acquiredDate, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageAcquiredDate(acquiredDate, imageIndex);
			}
		}
	}

	public void setImageAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageAnnotationRef(annotation, imageIndex, annotationRefIndex);
			}
		}
	}

	public void setImageDatasetRef(String dataset, int imageIndex, int datasetRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageDatasetRef(dataset, imageIndex, datasetRefIndex);
			}
		}
	}

	public void setImageDescription(String description, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageDescription(description, imageIndex);
			}
		}
	}

	public void setImageExperimentRef(String experiment, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageExperimentRef(experiment, imageIndex);
			}
		}
	}

	public void setImageExperimenterRef(String experimenter, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageExperimenterRef(experimenter, imageIndex);
			}
		}
	}

	public void setImageGroupRef(String group, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageGroupRef(group, imageIndex);
			}
		}
	}

	public void setImageID(String id, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageID(id, imageIndex);
			}
		}
	}

	// Ignoring ImagingEnvironment element, complex property
	public void setImageInstrumentRef(String instrument, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageInstrumentRef(instrument, imageIndex);
			}
		}
	}

	public void setImageMicrobeamManipulationRef(String microbeamManipulation, int imageIndex, int microbeamManipulationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageMicrobeamManipulationRef(microbeamManipulation, imageIndex, microbeamManipulationRefIndex);
			}
		}
	}

	public void setImageName(String name, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageName(name, imageIndex);
			}
		}
	}

	// Ignoring ObjectiveSettings element, complex property
	// Ignoring Pixels element, complex property
	public void setImageROIRef(String roi, int imageIndex, int ROIRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageROIRef(roi, imageIndex, ROIRefIndex);
			}
		}
	}

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

	public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
			}
		}
	}

	public void setImagingEnvironmentCO2Percent(PercentFraction co2percent, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImagingEnvironmentCO2Percent(co2percent, imageIndex);
			}
		}
	}

	public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImagingEnvironmentHumidity(humidity, imageIndex);
			}
		}
	}

	public void setImagingEnvironmentTemperature(Double temperature, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImagingEnvironmentTemperature(temperature, imageIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setInstrumentID(id, instrumentIndex);
			}
		}
	}

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
	public void setLaserID(String id, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserID(id, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setLaserLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserLotNumber(lotNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Manufacturer accessor from parent LightSource
	public void setLaserManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Model accessor from parent LightSource
	public void setLaserModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserModel(model, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Power accessor from parent LightSource
	public void setLaserPower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserPower(power, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// SerialNumber accessor from parent LightSource
	public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserPulse(Pulse pulse, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserPulse(pulse, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserPump(String pump, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserPump(pump, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserType(LaserType type, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserType(type, instrumentIndex, lightSourceIndex);
			}
		}
	}

	public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
			}
		}
	}

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
	public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodeID(id, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Ignoring Laser of parent abstract type
	// Ignoring LightEmittingDiode of parent abstract type
	// LotNumber accessor from parent LightSource
	public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodeLotNumber(lotNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Manufacturer accessor from parent LightSource
	public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodeManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Model accessor from parent LightSource
	public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodeModel(model, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Power accessor from parent LightSource
	public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodePower(power, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// SerialNumber accessor from parent LightSource
	public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodeSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	//
	// LightPath property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setLightPathDichroicRef(String dichroic, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightPathDichroicRef(dichroic, imageIndex, channelIndex);
			}
		}
	}

	public void setLightPathEmissionFilterRef(String emissionFilter, int imageIndex, int channelIndex, int emissionFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightPathEmissionFilterRef(emissionFilter, imageIndex, channelIndex, emissionFilterRefIndex);
			}
		}
	}

	public void setLightPathExcitationFilterRef(String excitationFilter, int imageIndex, int channelIndex, int excitationFilterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightPathExcitationFilterRef(excitationFilter, imageIndex, channelIndex, excitationFilterRefIndex);
			}
		}
	}

	//
	// LightSourceSettings property storage
	//
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}, u'MicrobeamManipulation': {u'Experiment': {u'OME': None}}}
	// Is multi path? True

	public void setChannelLightSourceSettingsAttenuation(PercentFraction attenuation, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelLightSourceSettingsAttenuation(attenuation, imageIndex, channelIndex);
			}
		}
	}

	public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationLightSourceSettingsAttenuation(attenuation, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
			}
		}
	}

	public void setChannelLightSourceSettingsID(String id, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelLightSourceSettingsID(id, imageIndex, channelIndex);
			}
		}
	}

	public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationLightSourceSettingsID(id, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
			}
		}
	}

	public void setChannelLightSourceSettingsWavelength(PositiveInteger wavelength, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelLightSourceSettingsWavelength(wavelength, imageIndex, channelIndex);
			}
		}
	}

	public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationLightSourceSettingsWavelength(wavelength, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
			}
		}
	}

	//
	// Line property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public void setLineDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setLineFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setLineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setLineID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setLineLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setLineName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setLineStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setLineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setLineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setLineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setLineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setLineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setLineTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLineX1(Double x1, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineX1(x1, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLineX2(Double x2, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineX2(x2, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLineY1(Double y1, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineY1(y1, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLineY2(Double y2, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineY2(y2, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// ListAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setListAnnotationAnnotationRef(String annotation, int listAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setListAnnotationAnnotationRef(annotation, listAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setListAnnotationDescription(String description, int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setListAnnotationDescription(description, listAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setListAnnotationID(String id, int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setListAnnotationID(id, listAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setListAnnotationNamespace(String namespace, int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setListAnnotationNamespace(namespace, listAnnotationIndex);
			}
		}
	}

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

	public void setLongAnnotationAnnotationRef(String annotation, int longAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLongAnnotationAnnotationRef(annotation, longAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setLongAnnotationDescription(String description, int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLongAnnotationDescription(description, longAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setLongAnnotationID(String id, int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLongAnnotationID(id, longAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLongAnnotationNamespace(namespace, longAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setLongAnnotationValue(Long value, int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLongAnnotationValue(value, longAnnotationIndex);
			}
		}
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public void setMaskDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setMaskFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setMaskFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setMaskID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setMaskLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setMaskName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setMaskStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setMaskStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setMaskStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setMaskTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setMaskTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setMaskTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setMaskTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring BinData element, complex property
	public void setMaskHeight(Double height, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskHeight(height, ROIIndex, shapeIndex);
			}
		}
	}

	public void setMaskWidth(Double width, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskWidth(width, ROIIndex, shapeIndex);
			}
		}
	}

	public void setMaskX(Double x, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskX(x, ROIIndex, shapeIndex);
			}
		}
	}

	public void setMaskY(Double y, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskY(y, ROIIndex, shapeIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationDescription(description, experimentIndex, microbeamManipulationIndex);
			}
		}
	}

	public void setMicrobeamManipulationExperimenterRef(String experimenter, int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationExperimenterRef(experimenter, experimentIndex, microbeamManipulationIndex);
			}
		}
	}

	public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationID(id, experimentIndex, microbeamManipulationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	// Ignoring LightSourceSettings element, complex property
	public void setMicrobeamManipulationROIRef(String roi, int experimentIndex, int microbeamManipulationIndex, int ROIRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationROIRef(roi, experimentIndex, microbeamManipulationIndex, ROIRefIndex);
			}
		}
	}

	public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicrobeamManipulationType(type, experimentIndex, microbeamManipulationIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicroscopeLotNumber(lotNumber, instrumentIndex);
			}
		}
	}

	public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicroscopeManufacturer(manufacturer, instrumentIndex);
			}
		}
	}

	public void setMicroscopeModel(String model, int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicroscopeModel(model, instrumentIndex);
			}
		}
	}

	public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicroscopeSerialNumber(serialNumber, instrumentIndex);
			}
		}
	}

	public void setMicroscopeType(MicroscopeType type, int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMicroscopeType(type, instrumentIndex);
			}
		}
	}

	//
	// OTF property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	// Ignoring BinaryFile element, complex property
	// Ignoring Channel_BackReference back reference
	public void setOTFFilterSetRef(String filterSet, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFFilterSetRef(filterSet, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setOTFID(String id, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFID(id, instrumentIndex, OTFIndex);
			}
		}
	}

	// Ignoring ObjectiveSettings element, complex property
	public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFSizeX(sizeX, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFSizeY(sizeY, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setOTFType(PixelType type, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFType(type, instrumentIndex, OTFIndex);
			}
		}
	}

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

	public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveID(id, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveIris(iris, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveLotNumber(lotNumber, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveModel(model, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveNominalMagnification(PositiveInteger nominalMagnification, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex);
			}
		}
	}

	public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
			}
		}
	}

	//
	// ObjectiveSettings property storage
	//
	// {u'Image': {u'OME': None}, u'OTF': {u'Instrument': {u'OME': None}}}
	// Is multi path? True

	public void setImageObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex);
			}
		}
	}

	public void setOTFObjectiveSettingsCorrectionCollar(Double correctionCollar, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFObjectiveSettingsCorrectionCollar(correctionCollar, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setImageObjectiveSettingsID(String id, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageObjectiveSettingsID(id, imageIndex);
			}
		}
	}

	public void setOTFObjectiveSettingsID(String id, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFObjectiveSettingsID(id, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setImageObjectiveSettingsMedium(Medium medium, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageObjectiveSettingsMedium(medium, imageIndex);
			}
		}
	}

	public void setOTFObjectiveSettingsMedium(Medium medium, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFObjectiveSettingsMedium(medium, instrumentIndex, OTFIndex);
			}
		}
	}

	public void setImageObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex);
			}
		}
	}

	public void setOTFObjectiveSettingsRefractiveIndex(Double refractiveIndex, int instrumentIndex, int OTFIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setOTFObjectiveSettingsRefractiveIndex(refractiveIndex, instrumentIndex, OTFIndex);
			}
		}
	}

	//
	// Path property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public void setPathDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setPathFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setPathFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setPathID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setPathLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setPathName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setPathStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setPathStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setPathStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setPathTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setPathTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setPathTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setPathTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPathDefinition(String definition, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPathDefinition(definition, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setPixelsAnnotationRef(String annotation, int imageIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsAnnotationRef(annotation, imageIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring BinData element, complex property
	// Ignoring Channel element, complex property
	public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsDimensionOrder(dimensionOrder, imageIndex);
			}
		}
	}

	public void setPixelsID(String id, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsID(id, imageIndex);
			}
		}
	}

	// Ignoring MetadataOnly element, complex property
	public void setPixelsPhysicalSizeX(PositiveFloat physicalSizeX, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsPhysicalSizeX(physicalSizeX, imageIndex);
			}
		}
	}

	public void setPixelsPhysicalSizeY(PositiveFloat physicalSizeY, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsPhysicalSizeY(physicalSizeY, imageIndex);
			}
		}
	}

	public void setPixelsPhysicalSizeZ(PositiveFloat physicalSizeZ, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsPhysicalSizeZ(physicalSizeZ, imageIndex);
			}
		}
	}

	// Ignoring Plane element, complex property
	public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsSizeC(sizeC, imageIndex);
			}
		}
	}

	public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsSizeT(sizeT, imageIndex);
			}
		}
	}

	public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsSizeX(sizeX, imageIndex);
			}
		}
	}

	public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsSizeY(sizeY, imageIndex);
			}
		}
	}

	public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsSizeZ(sizeZ, imageIndex);
			}
		}
	}

	// Ignoring TiffData element, complex property
	public void setPixelsTimeIncrement(Double timeIncrement, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsTimeIncrement(timeIncrement, imageIndex);
			}
		}
	}

	public void setPixelsType(PixelType type, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsType(type, imageIndex);
			}
		}
	}

	//
	// Plane property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setPlaneAnnotationRef(String annotation, int imageIndex, int planeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneAnnotationRef(annotation, imageIndex, planeIndex, annotationRefIndex);
			}
		}
	}

	public void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneDeltaT(deltaT, imageIndex, planeIndex);
			}
		}
	}

	public void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneExposureTime(exposureTime, imageIndex, planeIndex);
			}
		}
	}

	public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneHashSHA1(hashSHA1, imageIndex, planeIndex);
			}
		}
	}

	public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlanePositionX(positionX, imageIndex, planeIndex);
			}
		}
	}

	public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlanePositionY(positionY, imageIndex, planeIndex);
			}
		}
	}

	public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlanePositionZ(positionZ, imageIndex, planeIndex);
			}
		}
	}

	public void setPlaneTheC(NonNegativeInteger theC, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneTheC(theC, imageIndex, planeIndex);
			}
		}
	}

	public void setPlaneTheT(NonNegativeInteger theT, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneTheT(theT, imageIndex, planeIndex);
			}
		}
	}

	public void setPlaneTheZ(NonNegativeInteger theZ, int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlaneTheZ(theZ, imageIndex, planeIndex);
			}
		}
	}

	//
	// Plate property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setPlateAnnotationRef(String annotation, int plateIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAnnotationRef(annotation, plateIndex, annotationRefIndex);
			}
		}
	}

	public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateColumnNamingConvention(columnNamingConvention, plateIndex);
			}
		}
	}

	public void setPlateColumns(PositiveInteger columns, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateColumns(columns, plateIndex);
			}
		}
	}

	public void setPlateDescription(String description, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateDescription(description, plateIndex);
			}
		}
	}

	public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateExternalIdentifier(externalIdentifier, plateIndex);
			}
		}
	}

	public void setPlateID(String id, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateID(id, plateIndex);
			}
		}
	}

	public void setPlateName(String name, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateName(name, plateIndex);
			}
		}
	}

	// Ignoring PlateAcquisition element, complex property
	public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateRowNamingConvention(rowNamingConvention, plateIndex);
			}
		}
	}

	public void setPlateRows(PositiveInteger rows, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateRows(rows, plateIndex);
			}
		}
	}

	public void setPlateScreenRef(String screen, int plateIndex, int screenRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateScreenRef(screen, plateIndex, screenRefIndex);
			}
		}
	}

	public void setPlateStatus(String status, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateStatus(status, plateIndex);
			}
		}
	}

	// Ignoring Well element, complex property
	public void setPlateWellOriginX(Double wellOriginX, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateWellOriginX(wellOriginX, plateIndex);
			}
		}
	}

	public void setPlateWellOriginY(Double wellOriginY, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateWellOriginY(wellOriginY, plateIndex);
			}
		}
	}

	//
	// PlateAcquisition property storage
	//
	// {u'Plate': {u'OME': None}}
	// Is multi path? False

	public void setPlateAcquisitionAnnotationRef(String annotation, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionAnnotationRef(annotation, plateIndex, plateAcquisitionIndex, annotationRefIndex);
			}
		}
	}

	public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionDescription(description, plateIndex, plateAcquisitionIndex);
			}
		}
	}

	public void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionEndTime(endTime, plateIndex, plateAcquisitionIndex);
			}
		}
	}

	public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionID(id, plateIndex, plateAcquisitionIndex);
			}
		}
	}

	public void setPlateAcquisitionMaximumFieldCount(PositiveInteger maximumFieldCount, int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionMaximumFieldCount(maximumFieldCount, plateIndex, plateAcquisitionIndex);
			}
		}
	}

	public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionName(name, plateIndex, plateAcquisitionIndex);
			}
		}
	}

	public void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionStartTime(startTime, plateIndex, plateAcquisitionIndex);
			}
		}
	}

	public void setPlateAcquisitionWellSampleRef(String wellSample, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateAcquisitionWellSampleRef(wellSample, plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
			}
		}
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

	// Description accessor from parent Shape
	public void setPointDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setPointFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setPointFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setPointID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setPointLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setPointName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setPointStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setPointStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setPointStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setPointTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setPointTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setPointTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setPointTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPointX(Double x, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointX(x, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPointY(Double y, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointY(y, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public void setPolylineDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setPolylineFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setPolylineFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setPolylineID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setPolylineLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setPolylineName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setPolylineStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolylineStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setPolylineStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setPolylineTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setPolylineTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setPolylineTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setPolylineTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPolylineClosed(Boolean closed, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineClosed(closed, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPolylinePoints(String points, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylinePoints(points, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// Project property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setProjectAnnotationRef(String annotation, int projectIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectAnnotationRef(annotation, projectIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Dataset_BackReference back reference
	public void setProjectDescription(String description, int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectDescription(description, projectIndex);
			}
		}
	}

	public void setProjectExperimenterRef(String experimenter, int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectExperimenterRef(experimenter, projectIndex);
			}
		}
	}

	public void setProjectGroupRef(String group, int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectGroupRef(group, projectIndex);
			}
		}
	}

	public void setProjectID(String id, int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectID(id, projectIndex);
			}
		}
	}

	public void setProjectName(String name, int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectName(name, projectIndex);
			}
		}
	}

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

	public void setROIAnnotationRef(String annotation, int ROIIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setROIAnnotationRef(annotation, ROIIndex, annotationRefIndex);
			}
		}
	}

	public void setROIDescription(String description, int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setROIDescription(description, ROIIndex);
			}
		}
	}

	public void setROIID(String id, int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setROIID(id, ROIIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	// Ignoring MicrobeamManipulation_BackReference back reference
	public void setROIName(String name, int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setROIName(name, ROIIndex);
			}
		}
	}

	public void setROINamespace(String namespace, int ROIIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setROINamespace(namespace, ROIIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setReagentAnnotationRef(annotation, screenIndex, reagentIndex, annotationRefIndex);
			}
		}
	}

	public void setReagentDescription(String description, int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setReagentDescription(description, screenIndex, reagentIndex);
			}
		}
	}

	public void setReagentID(String id, int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setReagentID(id, screenIndex, reagentIndex);
			}
		}
	}

	public void setReagentName(String name, int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setReagentName(name, screenIndex, reagentIndex);
			}
		}
	}

	public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex);
			}
		}
	}

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
	public void setRectangleDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setRectangleFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setRectangleFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setRectangleID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setRectangleLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setRectangleName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setRectangleStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setRectangleStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setRectangleStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setRectangleTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setRectangleTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setRectangleTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setRectangleTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setRectangleHeight(Double height, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleHeight(height, ROIIndex, shapeIndex);
			}
		}
	}

	public void setRectangleWidth(Double width, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleWidth(width, ROIIndex, shapeIndex);
			}
		}
	}

	public void setRectangleX(Double x, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleX(x, ROIIndex, shapeIndex);
			}
		}
	}

	public void setRectangleY(Double y, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleY(y, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// Screen property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setScreenAnnotationRef(String annotation, int screenIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenAnnotationRef(annotation, screenIndex, annotationRefIndex);
			}
		}
	}

	public void setScreenDescription(String description, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenDescription(description, screenIndex);
			}
		}
	}

	public void setScreenID(String id, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenID(id, screenIndex);
			}
		}
	}

	public void setScreenName(String name, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenName(name, screenIndex);
			}
		}
	}

	public void setScreenPlateRef(String plate, int screenIndex, int plateRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenPlateRef(plate, screenIndex, plateRefIndex);
			}
		}
	}

	public void setScreenProtocolDescription(String protocolDescription, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenProtocolDescription(protocolDescription, screenIndex);
			}
		}
	}

	public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenProtocolIdentifier(protocolIdentifier, screenIndex);
			}
		}
	}

	// Ignoring Reagent element, complex property
	public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenReagentSetDescription(reagentSetDescription, screenIndex);
			}
		}
	}

	public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex);
			}
		}
	}

	public void setScreenType(String type, int screenIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setScreenType(type, screenIndex);
			}
		}
	}

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

	public void setStageLabelName(String name, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setStageLabelName(name, imageIndex);
			}
		}
	}

	public void setStageLabelX(Double x, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setStageLabelX(x, imageIndex);
			}
		}
	}

	public void setStageLabelY(Double y, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setStageLabelY(y, imageIndex);
			}
		}
	}

	public void setStageLabelZ(Double z, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setStageLabelZ(z, imageIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTagAnnotationAnnotationRef(annotation, tagAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTagAnnotationDescription(String description, int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTagAnnotationDescription(description, tagAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setTagAnnotationID(String id, int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTagAnnotationID(id, tagAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setTagAnnotationNamespace(String namespace, int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTagAnnotationNamespace(namespace, tagAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setTagAnnotationValue(String value, int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTagAnnotationValue(value, tagAnnotationIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTermAnnotationAnnotationRef(annotation, termAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTermAnnotationDescription(String description, int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTermAnnotationDescription(description, termAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setTermAnnotationID(String id, int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTermAnnotationID(id, termAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setTermAnnotationNamespace(String namespace, int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTermAnnotationNamespace(namespace, termAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setTermAnnotationValue(String value, int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTermAnnotationValue(value, termAnnotationIndex);
			}
		}
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
	//
	// Text property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// Description accessor from parent Shape
	public void setTextDescription(String description, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextDescription(description, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Ellipse of parent abstract type
	// Fill accessor from parent Shape
	public void setTextFill(Integer fill, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextFill(fill, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FillRule of parent abstract type
	// Ignoring FontFamily of parent abstract type
	// FontSize accessor from parent Shape
	public void setTextFontSize(NonNegativeInteger fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring FontStyle of parent abstract type
	// ID accessor from parent Shape
	public void setTextID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// Label accessor from parent Shape
	public void setTextLabel(String label, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextLabel(label, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Line of parent abstract type
	// Ignoring LineCap of parent abstract type
	// Ignoring MarkerEnd of parent abstract type
	// Ignoring MarkerStart of parent abstract type
	// Ignoring Mask of parent abstract type
	// Name accessor from parent Shape
	public void setTextName(String name, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextName(name, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Path of parent abstract type
	// Ignoring Point of parent abstract type
	// Ignoring Polyline of parent abstract type
	// Ignoring Rectangle of parent abstract type
	// Stroke accessor from parent Shape
	public void setTextStroke(Integer stroke, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextStroke(stroke, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setTextStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setTextStrokeWidth(Double strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Ignoring Text of parent abstract type
	// TheC accessor from parent Shape
	public void setTextTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setTextTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setTextTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setTextTransform(String transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	public void setTextValue(String value, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextValue(value, ROIIndex, shapeIndex);
			}
		}
	}

	public void setTextX(Double x, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextX(x, ROIIndex, shapeIndex);
			}
		}
	}

	public void setTextY(Double y, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTextY(y, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// TiffData property storage
	//
	// {u'Pixels': {u'Image': {u'OME': None}}}
	// Is multi path? False

	public void setTiffDataFirstC(NonNegativeInteger firstC, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTiffDataFirstC(firstC, imageIndex, tiffDataIndex);
			}
		}
	}

	public void setTiffDataFirstT(NonNegativeInteger firstT, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTiffDataFirstT(firstT, imageIndex, tiffDataIndex);
			}
		}
	}

	public void setTiffDataFirstZ(NonNegativeInteger firstZ, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTiffDataFirstZ(firstZ, imageIndex, tiffDataIndex);
			}
		}
	}

	public void setTiffDataIFD(NonNegativeInteger ifd, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTiffDataIFD(ifd, imageIndex, tiffDataIndex);
			}
		}
	}

	public void setTiffDataPlaneCount(NonNegativeInteger planeCount, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTiffDataPlaneCount(planeCount, imageIndex, tiffDataIndex);
			}
		}
	}

	// Ignoring UUID element, complex property
	//
	// TimestampAnnotation property storage
	//
	// {u'StructuredAnnotations': {u'OME': None}}
	// Is multi path? False

	public void setTimestampAnnotationAnnotationRef(String annotation, int timestampAnnotationIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTimestampAnnotationAnnotationRef(annotation, timestampAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setTimestampAnnotationDescription(String description, int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTimestampAnnotationDescription(description, timestampAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setTimestampAnnotationID(String id, int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTimestampAnnotationID(id, timestampAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTimestampAnnotationNamespace(namespace, timestampAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setTimestampAnnotationValue(String value, int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTimestampAnnotationValue(value, timestampAnnotationIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex);
			}
		}
	}

	public void setTransmittanceRangeCutInTolerance(NonNegativeInteger cutInTolerance, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex);
			}
		}
	}

	public void setTransmittanceRangeCutOut(PositiveInteger cutOut, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex);
			}
		}
	}

	public void setTransmittanceRangeCutOutTolerance(NonNegativeInteger cutOutTolerance, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex);
			}
		}
	}

	public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
			}
		}
	}

	//
	// UUID property storage
	//
	// {u'TiffData': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setUUIDFileName(fileName, imageIndex, tiffDataIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellAnnotationRef(annotation, plateIndex, wellIndex, annotationRefIndex);
			}
		}
	}

	public void setWellColor(Integer color, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellColor(color, plateIndex, wellIndex);
			}
		}
	}

	public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellColumn(column, plateIndex, wellIndex);
			}
		}
	}

	public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellExternalDescription(externalDescription, plateIndex, wellIndex);
			}
		}
	}

	public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex);
			}
		}
	}

	public void setWellID(String id, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellID(id, plateIndex, wellIndex);
			}
		}
	}

	public void setWellReagentRef(String reagent, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellReagentRef(reagent, plateIndex, wellIndex);
			}
		}
	}

	public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellRow(row, plateIndex, wellIndex);
			}
		}
	}

	public void setWellStatus(String status, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellStatus(status, plateIndex, wellIndex);
			}
		}
	}

	// Ignoring WellSample element, complex property
	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

	public void setWellSampleAnnotationRef(String annotation, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSampleAnnotationRef(annotation, plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
			}
		}
	}

	public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex);
			}
		}
	}

	public void setWellSampleImageRef(String image, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSampleImageRef(image, plateIndex, wellIndex, wellSampleIndex);
			}
		}
	}

	public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
			}
		}
	}

	// Ignoring PlateAcquisition_BackReference back reference
	public void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSamplePositionX(positionX, plateIndex, wellIndex, wellSampleIndex);
			}
		}
	}

	public void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSamplePositionY(positionY, plateIndex, wellIndex, wellSampleIndex);
			}
		}
	}

	public void setWellSampleTimepoint(String timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
			}
		}
	}

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setXMLAnnotationAnnotationRef(annotation, XMLAnnotationIndex, annotationRefIndex);
			}
		}
	}

	// Ignoring Channel_BackReference back reference
	// Ignoring Dataset_BackReference back reference
	public void setXMLAnnotationDescription(String description, int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setXMLAnnotationDescription(description, XMLAnnotationIndex);
			}
		}
	}

	// Ignoring Experimenter_BackReference back reference
	public void setXMLAnnotationID(String id, int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setXMLAnnotationID(id, XMLAnnotationIndex);
			}
		}
	}

	// Ignoring Image_BackReference back reference
	public void setXMLAnnotationNamespace(String namespace, int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setXMLAnnotationNamespace(namespace, XMLAnnotationIndex);
			}
		}
	}

	// Ignoring Pixels_BackReference back reference
	// Ignoring Plane_BackReference back reference
	// Ignoring PlateAcquisition_BackReference back reference
	// Ignoring Plate_BackReference back reference
	// Ignoring Project_BackReference back reference
	// Ignoring ROI_BackReference back reference
	// Ignoring Reagent_BackReference back reference
	// Ignoring Screen_BackReference back reference
	public void setXMLAnnotationValue(String value, int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setXMLAnnotationValue(value, XMLAnnotationIndex);
			}
		}
	}

	// Ignoring WellSample_BackReference back reference
	// Ignoring Well_BackReference back reference
}
