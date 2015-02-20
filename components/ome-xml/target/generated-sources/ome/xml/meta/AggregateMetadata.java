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

import java.util.Iterator;
import java.util.List;

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A metadata store which delegates the actual storage to one or more <i>sub</i>
 * metadata stores.
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AggregateMetadata implements IMetadata
{
	// -- Fields --

	/** The active metadata store delegates. */
	private List<BaseMetadata> delegates;

	// -- Constructor --

	/**
	 * Creates a new instance.
	 * @param delegates of type {@link MetadataRetrieve}
	 *   and/or {@link MetadataStore}.
	 */
	public AggregateMetadata(List<BaseMetadata> delegates)
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
	public MetadataRoot getRoot()
	{
		throw new RuntimeException("Unsupported by AggregateMetadata");
	}

	/**
	 * Unsupported with an AggregateMetadata.
	 * @throws RuntimeException Always.
	 */
	public void setRoot(MetadataRoot root)
	{
		throw new RuntimeException("Unsupported by AggregateMetadata");
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

  public int getBooleanAnnotationAnnotationCount(int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getBooleanAnnotationAnnotationCount(booleanAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getCommentAnnotationAnnotationCount(int commentAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getCommentAnnotationAnnotationCount(commentAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getDoubleAnnotationAnnotationCount(int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getDoubleAnnotationAnnotationCount(doubleAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getFileAnnotationAnnotationCount(int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getFileAnnotationAnnotationCount(fileAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getListAnnotationAnnotationCount(int listAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getListAnnotationAnnotationCount(listAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getLongAnnotationAnnotationCount(int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getLongAnnotationAnnotationCount(longAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getMapAnnotationAnnotationCount(int mapAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getMapAnnotationAnnotationCount(mapAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getTagAnnotationAnnotationCount(int tagAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getTagAnnotationAnnotationCount(tagAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getTermAnnotationAnnotationCount(int termAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getTermAnnotationAnnotationCount(termAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getTimestampAnnotationAnnotationCount(int timestampAnnotationIndex)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getTimestampAnnotationAnnotationCount(
          timestampAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  public int getXMLAnnotationAnnotationCount(int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result =
          retrieve.getXMLAnnotationAnnotationCount(xmlAnnotationIndex);
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

  public String getShapeType(int roiIndex, int shapeIndex)
  {
    for (Iterator iter = delegates.iterator(); iter.hasNext();)
    {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve)
      {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        return retrieve.getShapeType(roiIndex, shapeIndex);
      }
    }
    return null;
  }

	// -- Entity counting (code generated definitions) --

	// AnnotationRef entity counting
	public int getLightSourceAnnotationRefCount(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLightSourceAnnotationRefCount(instrumentIndex, lightSourceIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getInstrumentAnnotationRefCount(int instrumentIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getInstrumentAnnotationRefCount(instrumentIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getObjectiveAnnotationRefCount(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getObjectiveAnnotationRefCount(instrumentIndex, objectiveIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	public int getDetectorAnnotationRefCount(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDetectorAnnotationRefCount(instrumentIndex, detectorIndex);
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

	public int getExperimenterGroupAnnotationRefCount(int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimenterGroupAnnotationRefCount(experimenterGroupIndex);
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

	public int getDichroicAnnotationRefCount(int instrumentIndex, int dichroicIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDichroicAnnotationRefCount(instrumentIndex, dichroicIndex);
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

	public int getFilterAnnotationRefCount(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getFilterAnnotationRefCount(instrumentIndex, filterIndex);
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

	public int getLightPathAnnotationRefCount(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLightPathAnnotationRefCount(imageIndex, channelIndex);
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

	public int getShapeAnnotationRefCount(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getShapeAnnotationRefCount(ROIIndex, shapeIndex);
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
	public int getDatasetRefCount(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDatasetRefCount(projectIndex);
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

	// ExperimenterGroup entity counting
	public int getExperimenterGroupCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimenterGroupCount();
				if (result >= 0) return result;
			}
		}
		return -1;
	}

	// ExperimenterGroupRef entity counting
	// ExperimenterRef entity counting
	public int getExperimenterGroupExperimenterRefCount(int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getExperimenterGroupExperimenterRefCount(experimenterGroupIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

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
	// GenericExcitationSource entity counting
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
	public int getDatasetImageRefCount(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getDatasetImageRefCount(datasetIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

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
	// Label entity counting
	// Laser entity counting
	// Leader entity counting
	public int getLeaderCount(int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getLeaderCount(experimenterGroupIndex);
				if (result >= 0) return result;
			}
		}
		return -1;
	}

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

	// Map entity counting
	// MapAnnotation entity counting
	public int getMapAnnotationCount()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				int result = retrieve.getMapAnnotationCount();
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
	// Polygon entity counting
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
	// Rights entity counting
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
	// {u'TiffData': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
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

	/** Gets the Map value associated with this annotation */
	public java.util.List<ome.xml.model.MapPair> getMapAnnotationValue(int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				java.util.List<ome.xml.model.MapPair> result = retrieve.getMapAnnotationValue(mapAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	/** Gets the Map value associated with this generic light source */
	public java.util.List<ome.xml.model.MapPair> getGenericExcitationSourceMap(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				java.util.List<ome.xml.model.MapPair> result = retrieve.getGenericExcitationSourceMap(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	/** Gets the Map value associated with this imaging environment */
	public java.util.List<ome.xml.model.MapPair> getImagingEnvironmentMap(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				java.util.List<ome.xml.model.MapPair> result = retrieve.getImagingEnvironmentMap(imageIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getArcAnnotationRef(instrumentIndex, lightSourceIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public Power getArcPower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Power result = retrieve.getArcPower(instrumentIndex, lightSourceIndex);
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
	// Indexes: {u'FileAnnotation': [{'argname': u'fileAnnotationIndex', 'argtype': 'int', 'type': u'FileAnnotation'}]}
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	public String getBinaryFileFileName(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBinaryFileFileName(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getBinaryFileMIMEType(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBinaryFileMIMEType(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public NonNegativeLong getBinaryFileSize(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeLong result = retrieve.getBinaryFileSize(fileAnnotationIndex);
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

	public String getBinaryOnlyMetadataFile()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBinaryOnlyMetadataFile();
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getBinaryOnlyUUID()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBinaryOnlyUUID();
				if (result != null) return result;
			}
		}
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

	public String getBooleanAnnotationAnnotator(int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getBooleanAnnotationAnnotator(booleanAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// Channel property storage
	//
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
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

	public Color getChannelColor(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getChannelColor(imageIndex, channelIndex);
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

	public Length getChannelEmissionWavelength(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getChannelEmissionWavelength(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getChannelExcitationWavelength(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getChannelExcitationWavelength(imageIndex, channelIndex);
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

	public Length getChannelPinholeSize(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getChannelPinholeSize(imageIndex, channelIndex);
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
	// Indexes: {u'StructuredAnnotations': [{'argname': u'commentAnnotationIndex', 'argtype': 'int', 'type': u'CommentAnnotation'}]}
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

	public String getCommentAnnotationAnnotator(int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getCommentAnnotationAnnotator(commentAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// Dataset property storage
	//
	// Indexes: {u'OME': [{'argname': u'datasetIndex', 'argtype': 'int', 'type': u'Dataset'}]}
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

	public String getDatasetExperimenterGroupRef(int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetExperimenterGroupRef(datasetIndex);
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

	public String getDatasetImageRef(int datasetIndex, int imageRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDatasetImageRef(datasetIndex, imageRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public String getDetectorAnnotationRef(int instrumentIndex, int detectorIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDetectorAnnotationRef(instrumentIndex, detectorIndex, annotationRefIndex);
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

	public ElectricPotential getDetectorVoltage(int instrumentIndex, int detectorIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				ElectricPotential result = retrieve.getDetectorVoltage(instrumentIndex, detectorIndex);
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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
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

	public PositiveInteger getDetectorSettingsIntegration(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getDetectorSettingsIntegration(imageIndex, channelIndex);
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

	public Frequency getDetectorSettingsReadOutRate(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Frequency result = retrieve.getDetectorSettingsReadOutRate(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public ElectricPotential getDetectorSettingsVoltage(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				ElectricPotential result = retrieve.getDetectorSettingsVoltage(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getDetectorSettingsZoom(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getDetectorSettingsZoom(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDichroicAnnotationRef(instrumentIndex, dichroicIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public String getDoubleAnnotationAnnotator(int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getDoubleAnnotationAnnotator(doubleAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// Ellipse property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getEllipseAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getEllipseFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getEllipseFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getEllipseFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getEllipseFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getEllipseFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getEllipseFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getEllipseFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getEllipseFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getEllipseFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getEllipseFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	// LineCap accessor from parent Shape
	public LineCap getEllipseLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getEllipseLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getEllipseLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getEllipseLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getEllipseStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getEllipseStrokeColor(ROIIndex, shapeIndex);
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
	public Length getEllipseStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getEllipseStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getEllipseText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getEllipseText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public AffineTransform getEllipseTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getEllipseTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getEllipseVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getEllipseVisible(ROIIndex, shapeIndex);
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
	// ExperimenterGroup property storage
	//
	// Indexes: {u'OME': [{'argname': u'experimenterGroupIndex', 'argtype': 'int', 'type': u'ExperimenterGroup'}]}
	// {u'OME': None}
	// Is multi path? False

	public String getExperimenterGroupAnnotationRef(int experimenterGroupIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupAnnotationRef(experimenterGroupIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterGroupDescription(int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupDescription(experimenterGroupIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterGroupExperimenterRef(int experimenterGroupIndex, int experimenterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupExperimenterRef(experimenterGroupIndex, experimenterRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterGroupID(int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupID(experimenterGroupIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterGroupLeader(int experimenterGroupIndex, int leaderIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupLeader(experimenterGroupIndex, leaderIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getExperimenterGroupName(int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getExperimenterGroupName(experimenterGroupIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilamentAnnotationRef(instrumentIndex, lightSourceIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public Power getFilamentPower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Power result = retrieve.getFilamentPower(instrumentIndex, lightSourceIndex);
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
	// Indexes: {u'StructuredAnnotations': [{'argname': u'fileAnnotationIndex', 'argtype': 'int', 'type': u'FileAnnotation'}]}
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

	public String getFileAnnotationAnnotator(int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFileAnnotationAnnotator(fileAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// Filter property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterIndex', 'argtype': 'int', 'type': u'Filter'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public String getFilterAnnotationRef(int instrumentIndex, int filterIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getFilterAnnotationRef(instrumentIndex, filterIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'filterSetIndex', 'argtype': 'int', 'type': u'FilterSet'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGenericExcitationSourceAnnotationRef(instrumentIndex, lightSourceIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// ID accessor from parent LightSource
	public String getGenericExcitationSourceID(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGenericExcitationSourceID(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// LotNumber accessor from parent LightSource
	public String getGenericExcitationSourceLotNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGenericExcitationSourceLotNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Manufacturer accessor from parent LightSource
	public String getGenericExcitationSourceManufacturer(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGenericExcitationSourceManufacturer(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Model accessor from parent LightSource
	public String getGenericExcitationSourceModel(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGenericExcitationSourceModel(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Power accessor from parent LightSource
	public Power getGenericExcitationSourcePower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Power result = retrieve.getGenericExcitationSourcePower(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// SerialNumber accessor from parent LightSource
	public String getGenericExcitationSourceSerialNumber(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getGenericExcitationSourceSerialNumber(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Timestamp result = retrieve.getImageAcquisitionDate(imageIndex);
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

	public String getImageExperimenterGroupRef(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getImageExperimenterGroupRef(imageIndex);
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Pressure result = retrieve.getImagingEnvironmentAirPressure(imageIndex);
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

	public Temperature getImagingEnvironmentTemperature(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Temperature result = retrieve.getImagingEnvironmentTemperature(imageIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getInstrumentAnnotationRef(instrumentIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLabelAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getLabelFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getLabelFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getLabelFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getLabelFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getLabelFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getLabelFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getLabelFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getLabelFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getLabelFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getLabelFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// ID accessor from parent Shape
	public String getLabelID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLabelID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// LineCap accessor from parent Shape
	public LineCap getLabelLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getLabelLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getLabelLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getLabelLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getLabelStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getLabelStrokeColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getLabelStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLabelStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Length getLabelStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getLabelStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getLabelText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLabelText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getLabelTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLabelTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getLabelTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLabelTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getLabelTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getLabelTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getLabelTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getLabelTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getLabelVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getLabelVisible(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLabelX(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLabelX(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getLabelY(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getLabelY(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLaserAnnotationRef(instrumentIndex, lightSourceIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public Power getLaserPower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Power result = retrieve.getLaserPower(instrumentIndex, lightSourceIndex);
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

	public Frequency getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Frequency result = retrieve.getLaserRepetitionRate(instrumentIndex, lightSourceIndex);
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

	public Length getLaserWavelength(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getLaserWavelength(instrumentIndex, lightSourceIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightEmittingDiodeAnnotationRef(instrumentIndex, lightSourceIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public Power getLightEmittingDiodePower(int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Power result = retrieve.getLightEmittingDiodePower(instrumentIndex, lightSourceIndex);
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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}]}
	// {u'Channel': {u'Pixels': {u'Image': {u'OME': None}}}}
	// Is multi path? False

	public String getLightPathAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLightPathAnnotationRef(imageIndex, channelIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	// Indexes: {u'Channel': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'channelIndex', 'argtype': 'int', 'type': u'Channel'}], u'MicrobeamManipulation': [{'argname': u'experimentIndex', 'argtype': 'int', 'type': u'Experiment'}, {'argname': u'microbeamManipulationIndex', 'argtype': 'int', 'type': u'MicrobeamManipulation'}, {'argname': u'lightSourceSettingsIndex', 'argtype': 'int', 'type': u'LightSourceSettings'}]}
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

	public Length getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getChannelLightSourceSettingsWavelength(imageIndex, channelIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getMicrobeamManipulationLightSourceSettingsWavelength(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getLineFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getLineFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getLineFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getLineFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getLineFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getLineFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getLineFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getLineFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getLineFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getLineFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	// LineCap accessor from parent Shape
	public LineCap getLineLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getLineLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getLineLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getLineLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getLineStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getLineStrokeColor(ROIIndex, shapeIndex);
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
	public Length getLineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getLineStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getLineText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLineText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public AffineTransform getLineTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getLineTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getLineVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getLineVisible(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Marker getLineMarkerEnd(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Marker result = retrieve.getLineMarkerEnd(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Marker getLineMarkerStart(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Marker result = retrieve.getLineMarkerStart(ROIIndex, shapeIndex);
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
	// Indexes: {u'StructuredAnnotations': [{'argname': u'listAnnotationIndex', 'argtype': 'int', 'type': u'ListAnnotation'}]}
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

	public String getListAnnotationAnnotator(int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getListAnnotationAnnotator(listAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// LongAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'longAnnotationIndex', 'argtype': 'int', 'type': u'LongAnnotation'}]}
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

	public String getLongAnnotationAnnotator(int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getLongAnnotationAnnotator(longAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMapAnnotationAnnotationRef(mapAnnotationIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMapAnnotationAnnotator(int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMapAnnotationAnnotator(mapAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMapAnnotationDescription(int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMapAnnotationDescription(mapAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMapAnnotationID(int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMapAnnotationID(mapAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getMapAnnotationNamespace(int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMapAnnotationNamespace(mapAnnotationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getMaskFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getMaskFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getMaskFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getMaskFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getMaskFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getMaskFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getMaskFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getMaskFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getMaskFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getMaskFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	// LineCap accessor from parent Shape
	public LineCap getMaskLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getMaskLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getMaskLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getMaskLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getMaskStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getMaskStrokeColor(ROIIndex, shapeIndex);
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
	public Length getMaskStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getMaskStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getMaskText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getMaskText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public AffineTransform getMaskTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getMaskTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getMaskVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getMaskVisible(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	// Objective property storage
	//
	// Indexes: {u'Instrument': [{'argname': u'instrumentIndex', 'argtype': 'int', 'type': u'Instrument'}, {'argname': u'objectiveIndex', 'argtype': 'int', 'type': u'Objective'}]}
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public String getObjectiveAnnotationRef(int instrumentIndex, int objectiveIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveAnnotationRef(instrumentIndex, objectiveIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public Double getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
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

	public Length getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getObjectiveSettingsCorrectionCollar(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getObjectiveSettingsID(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getObjectiveSettingsID(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Medium getObjectiveSettingsMedium(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Medium result = retrieve.getObjectiveSettingsMedium(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Double getObjectiveSettingsRefractiveIndex(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Double result = retrieve.getObjectiveSettingsRefractiveIndex(imageIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPixelsBigEndian(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public Boolean getPixelsInterleaved(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPixelsInterleaved(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getPixelsPhysicalSizeX(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPixelsPhysicalSizeX(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getPixelsPhysicalSizeY(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPixelsPhysicalSizeY(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getPixelsPhysicalSizeZ(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPixelsPhysicalSizeZ(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public PositiveInteger getPixelsSignificantBits(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				PositiveInteger result = retrieve.getPixelsSignificantBits(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public Time getPixelsTimeIncrement(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Time result = retrieve.getPixelsTimeIncrement(imageIndex);
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
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'planeIndex', 'argtype': 'int', 'type': u'Plane'}]}
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

	public Time getPlaneDeltaT(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Time result = retrieve.getPlaneDeltaT(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Time getPlaneExposureTime(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Time result = retrieve.getPlaneExposureTime(imageIndex, planeIndex);
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

	public Length getPlanePositionX(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPlanePositionX(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getPlanePositionY(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPlanePositionY(imageIndex, planeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getPlanePositionZ(int imageIndex, int planeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPlanePositionZ(imageIndex, planeIndex);
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
	// Indexes: {u'OME': [{'argname': u'plateIndex', 'argtype': 'int', 'type': u'Plate'}]}
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

	public NonNegativeInteger getPlateFieldIndex(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPlateFieldIndex(plateIndex);
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

	public Length getPlateWellOriginX(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPlateWellOriginX(plateIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getPlateWellOriginY(int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPlateWellOriginY(plateIndex);
				if (result != null) return result;
			}
		}
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

	public Timestamp getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Timestamp result = retrieve.getPlateAcquisitionEndTime(plateIndex, plateAcquisitionIndex);
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

	public Timestamp getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Timestamp result = retrieve.getPlateAcquisitionStartTime(plateIndex, plateAcquisitionIndex);
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getPointFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getPointFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getPointFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getPointFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPointFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getPointFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getPointFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPointFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPointFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getPointFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	// LineCap accessor from parent Shape
	public LineCap getPointLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getPointLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getPointLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPointLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getPointStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getPointStrokeColor(ROIIndex, shapeIndex);
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
	public Length getPointStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPointStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getPointText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPointText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public AffineTransform getPointTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getPointTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getPointVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPointVisible(ROIIndex, shapeIndex);
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
	// Polygon property storage
	//
	// Indexes: {u'Shape': [{'argname': u'ROIIndex', 'argtype': 'int', 'type': u'ROI'}, {'argname': u'shapeIndex', 'argtype': 'int', 'type': u'Shape'}]}
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public String getPolygonAnnotationRef(int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolygonAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getPolygonFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getPolygonFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getPolygonFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getPolygonFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPolygonFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getPolygonFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getPolygonFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPolygonFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPolygonFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getPolygonFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// ID accessor from parent Shape
	public String getPolygonID(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolygonID(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// LineCap accessor from parent Shape
	public LineCap getPolygonLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getPolygonLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getPolygonLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPolygonLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getPolygonStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getPolygonStrokeColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeDashArray accessor from parent Shape
	public String getPolygonStrokeDashArray(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolygonStrokeDashArray(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeWidth accessor from parent Shape
	public Length getPolygonStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPolygonStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getPolygonText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolygonText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheC accessor from parent Shape
	public NonNegativeInteger getPolygonTheC(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolygonTheC(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheT accessor from parent Shape
	public NonNegativeInteger getPolygonTheT(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolygonTheT(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// TheZ accessor from parent Shape
	public NonNegativeInteger getPolygonTheZ(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				NonNegativeInteger result = retrieve.getPolygonTheZ(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Transform accessor from parent Shape
	public AffineTransform getPolygonTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getPolygonTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getPolygonVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPolygonVisible(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getPolygonPoints(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolygonPoints(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getPolylineFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getPolylineFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getPolylineFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getPolylineFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getPolylineFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getPolylineFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getPolylineFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPolylineFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getPolylineFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getPolylineFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	// LineCap accessor from parent Shape
	public LineCap getPolylineLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getPolylineLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getPolylineLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPolylineLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getPolylineStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getPolylineStrokeColor(ROIIndex, shapeIndex);
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
	public Length getPolylineStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getPolylineStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getPolylineText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getPolylineText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public AffineTransform getPolylineTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getPolylineTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getPolylineVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getPolylineVisible(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Marker getPolylineMarkerEnd(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Marker result = retrieve.getPolylineMarkerEnd(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Marker getPolylineMarkerStart(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Marker result = retrieve.getPolylineMarkerStart(ROIIndex, shapeIndex);
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
	// Indexes: {u'OME': [{'argname': u'projectIndex', 'argtype': 'int', 'type': u'Project'}]}
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

	public String getProjectDatasetRef(int projectIndex, int datasetRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectDatasetRef(projectIndex, datasetRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public String getProjectExperimenterGroupRef(int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getProjectExperimenterGroupRef(projectIndex);
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleAnnotationRef(ROIIndex, shapeIndex, annotationRefIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillColor accessor from parent Shape
	public Color getRectangleFillColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getRectangleFillColor(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FillRule accessor from parent Shape
	public FillRule getRectangleFillRule(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FillRule result = retrieve.getRectangleFillRule(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontFamily accessor from parent Shape
	public FontFamily getRectangleFontFamily(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontFamily result = retrieve.getRectangleFontFamily(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontSize accessor from parent Shape
	public Length getRectangleFontSize(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getRectangleFontSize(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// FontStyle accessor from parent Shape
	public FontStyle getRectangleFontStyle(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				FontStyle result = retrieve.getRectangleFontStyle(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	// LineCap accessor from parent Shape
	public LineCap getRectangleLineCap(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				LineCap result = retrieve.getRectangleLineCap(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Locked accessor from parent Shape
	public Boolean getRectangleLocked(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getRectangleLocked(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// StrokeColor accessor from parent Shape
	public Color getRectangleStrokeColor(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getRectangleStrokeColor(ROIIndex, shapeIndex);
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
	public Length getRectangleStrokeWidth(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getRectangleStrokeWidth(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Text accessor from parent Shape
	public String getRectangleText(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRectangleText(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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
	public AffineTransform getRectangleTransform(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				AffineTransform result = retrieve.getRectangleTransform(ROIIndex, shapeIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	// Visible accessor from parent Shape
	public Boolean getRectangleVisible(int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Boolean result = retrieve.getRectangleVisible(ROIIndex, shapeIndex);
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
	// Rights property storage
	//
	// Indexes: {u'OME': []}
	// {u'OME': None}
	// Is multi path? False

	public String getRightsRightsHeld()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRightsRightsHeld();
				if (result != null) return result;
			}
		}
		return null;
	}

	public String getRightsRightsHolder()
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getRightsRightsHolder();
				if (result != null) return result;
			}
		}
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
	// StageLabel property storage
	//
	// Indexes: {u'Image': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}]}
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

	public Length getStageLabelX(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getStageLabelX(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getStageLabelY(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getStageLabelY(imageIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getStageLabelZ(int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getStageLabelZ(imageIndex);
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

	//
	// TagAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'tagAnnotationIndex', 'argtype': 'int', 'type': u'TagAnnotation'}]}
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

	public String getTagAnnotationAnnotator(int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTagAnnotationAnnotator(tagAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// TermAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'termAnnotationIndex', 'argtype': 'int', 'type': u'TermAnnotation'}]}
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

	public String getTermAnnotationAnnotator(int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTermAnnotationAnnotator(termAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	//
	// TiffData property storage
	//
	// Indexes: {u'Pixels': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
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

	//
	// TimestampAnnotation property storage
	//
	// Indexes: {u'StructuredAnnotations': [{'argname': u'timestampAnnotationIndex', 'argtype': 'int', 'type': u'TimestampAnnotation'}]}
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

	public String getTimestampAnnotationAnnotator(int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getTimestampAnnotationAnnotator(timestampAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	public Timestamp getTimestampAnnotationValue(int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Timestamp result = retrieve.getTimestampAnnotationValue(timestampAnnotationIndex);
				if (result != null) return result;
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
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
	// Indexes: {u'TiffData': [{'argname': u'imageIndex', 'argtype': 'int', 'type': u'Image'}, {'argname': u'tiffDataIndex', 'argtype': 'int', 'type': u'TiffData'}]}
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

	public Color getWellColor(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Color result = retrieve.getWellColor(plateIndex, wellIndex);
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

	public String getWellType(int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getWellType(plateIndex, wellIndex);
				if (result != null) return result;
			}
		}
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

	public Length getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getWellSamplePositionX(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Length getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Length result = retrieve.getWellSamplePositionY(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

	public Timestamp getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				Timestamp result = retrieve.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
				if (result != null) return result;
			}
		}
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

	public String getXMLAnnotationAnnotator(int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataRetrieve)
			{
				MetadataRetrieve retrieve = (MetadataRetrieve) o;
				String result = retrieve.getXMLAnnotationAnnotator(XMLAnnotationIndex);
				if (result != null) return result;
			}
		}
		return null;
	}

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

	/** Sets the Map value associated with this annotation */
	public void setMapAnnotationValue(java.util.List<ome.xml.model.MapPair> value, int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMapAnnotationValue(value, mapAnnotationIndex);
			}
		}
	}

	/** Sets the Map value associated with this generic light source */
	public void setGenericExcitationSourceMap(java.util.List<ome.xml.model.MapPair> map, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceMap(map, instrumentIndex, lightSourceIndex);
			}
		}
	}

	/** Sets the Map value associated with this imaging environment */
	public void setImagingEnvironmentMap(java.util.List<ome.xml.model.MapPair> map, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImagingEnvironmentMap(map, imageIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setArcAnnotationRef(annotation, instrumentIndex, lightSourceIndex, annotationRefIndex);
			}
		}
	}

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
	public void setArcPower(Power power, int instrumentIndex, int lightSourceIndex)
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
	// {u'FileAnnotation': {u'StructuredAnnotations': {u'OME': None}}}
	// Is multi path? False

	public void setBinaryFileFileName(String fileName, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBinaryFileFileName(fileName, fileAnnotationIndex);
			}
		}
	}

	public void setBinaryFileMIMEType(String mimeType, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBinaryFileMIMEType(mimeType, fileAnnotationIndex);
			}
		}
	}

	public void setBinaryFileSize(NonNegativeLong size, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBinaryFileSize(size, fileAnnotationIndex);
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

	public void setBooleanAnnotationAnnotator(String annotator, int booleanAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setBooleanAnnotationAnnotator(annotator, booleanAnnotationIndex);
			}
		}
	}

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

	public void setChannelColor(Color color, int imageIndex, int channelIndex)
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

	public void setChannelEmissionWavelength(Length emissionWavelength, int imageIndex, int channelIndex)
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

	public void setChannelExcitationWavelength(Length excitationWavelength, int imageIndex, int channelIndex)
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

	public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setChannelNDFilter(ndFilter, imageIndex, channelIndex);
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

	public void setChannelPinholeSize(Length pinholeSize, int imageIndex, int channelIndex)
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

	public void setCommentAnnotationAnnotator(String annotator, int commentAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setCommentAnnotationAnnotator(annotator, commentAnnotationIndex);
			}
		}
	}

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

	public void setDatasetExperimenterGroupRef(String experimenterGroup, int datasetIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetExperimenterGroupRef(experimenterGroup, datasetIndex);
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

	public void setDatasetImageRef(String image, int datasetIndex, int imageRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDatasetImageRef(image, datasetIndex, imageRefIndex);
			}
		}
	}

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

	public void setDetectorAnnotationRef(String annotation, int instrumentIndex, int detectorIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorAnnotationRef(annotation, instrumentIndex, detectorIndex, annotationRefIndex);
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

	public void setDetectorVoltage(ElectricPotential voltage, int instrumentIndex, int detectorIndex)
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

	public void setDetectorSettingsIntegration(PositiveInteger integration, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsIntegration(integration, imageIndex, channelIndex);
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

	public void setDetectorSettingsReadOutRate(Frequency readOutRate, int imageIndex, int channelIndex)
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

	public void setDetectorSettingsVoltage(ElectricPotential voltage, int imageIndex, int channelIndex)
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

	public void setDetectorSettingsZoom(Double zoom, int imageIndex, int channelIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDetectorSettingsZoom(zoom, imageIndex, channelIndex);
			}
		}
	}

	//
	// Dichroic property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setDichroicAnnotationRef(String annotation, int instrumentIndex, int dichroicIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDichroicAnnotationRef(annotation, instrumentIndex, dichroicIndex, annotationRefIndex);
			}
		}
	}

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

	public void setDoubleAnnotationAnnotator(String annotator, int doubleAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setDoubleAnnotationAnnotator(annotator, doubleAnnotationIndex);
			}
		}
	}

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

	//
	// Ellipse property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setEllipseAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setEllipseFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setEllipseFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setEllipseFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setEllipseFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// FontStyle accessor from parent Shape
	public void setEllipseFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

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

	// LineCap accessor from parent Shape
	public void setEllipseLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setEllipseLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setEllipseStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseStrokeColor(strokeColor, ROIIndex, shapeIndex);
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
	public void setEllipseStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Text accessor from parent Shape
	public void setEllipseText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseText(text, ROIIndex, shapeIndex);
			}
		}
	}

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
	public void setEllipseTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setEllipseVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setEllipseVisible(visible, ROIIndex, shapeIndex);
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
	// ExperimenterGroup property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setExperimenterGroupAnnotationRef(String annotation, int experimenterGroupIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupAnnotationRef(annotation, experimenterGroupIndex, annotationRefIndex);
			}
		}
	}

	public void setExperimenterGroupDescription(String description, int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupDescription(description, experimenterGroupIndex);
			}
		}
	}

	public void setExperimenterGroupExperimenterRef(String experimenter, int experimenterGroupIndex, int experimenterRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupExperimenterRef(experimenter, experimenterGroupIndex, experimenterRefIndex);
			}
		}
	}

	public void setExperimenterGroupID(String id, int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupID(id, experimenterGroupIndex);
			}
		}
	}

	public void setExperimenterGroupLeader(String leader, int experimenterGroupIndex, int leaderIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupLeader(leader, experimenterGroupIndex, leaderIndex);
			}
		}
	}

	public void setExperimenterGroupName(String name, int experimenterGroupIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setExperimenterGroupName(name, experimenterGroupIndex);
			}
		}
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilamentAnnotationRef(annotation, instrumentIndex, lightSourceIndex, annotationRefIndex);
			}
		}
	}

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
	public void setFilamentPower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setFileAnnotationAnnotator(String annotator, int fileAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFileAnnotationAnnotator(annotator, fileAnnotationIndex);
			}
		}
	}

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

	//
	// Filter property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setFilterAnnotationRef(String annotation, int instrumentIndex, int filterIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setFilterAnnotationRef(annotation, instrumentIndex, filterIndex, annotationRefIndex);
			}
		}
	}

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceAnnotationRef(annotation, instrumentIndex, lightSourceIndex, annotationRefIndex);
			}
		}
	}

	// ID accessor from parent LightSource
	public void setGenericExcitationSourceID(String id, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceID(id, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// LotNumber accessor from parent LightSource
	public void setGenericExcitationSourceLotNumber(String lotNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceLotNumber(lotNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Manufacturer accessor from parent LightSource
	public void setGenericExcitationSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Model accessor from parent LightSource
	public void setGenericExcitationSourceModel(String model, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceModel(model, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// Power accessor from parent LightSource
	public void setGenericExcitationSourcePower(Power power, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourcePower(power, instrumentIndex, lightSourceIndex);
			}
		}
	}

	// SerialNumber accessor from parent LightSource
	public void setGenericExcitationSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setGenericExcitationSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
			}
		}
	}

	//
	// Image property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setImageAcquisitionDate(Timestamp acquisitionDate, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageAcquisitionDate(acquisitionDate, imageIndex);
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

	public void setImageExperimenterGroupRef(String experimenterGroup, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImageExperimenterGroupRef(experimenterGroup, imageIndex);
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

	public void setImagingEnvironmentCO2Percent(PercentFraction co2Percent, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setImagingEnvironmentCO2Percent(co2Percent, imageIndex);
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

	public void setImagingEnvironmentTemperature(Temperature temperature, int imageIndex)
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

	public void setInstrumentAnnotationRef(String annotation, int instrumentIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setInstrumentAnnotationRef(annotation, instrumentIndex, annotationRefIndex);
			}
		}
	}

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setLabelFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setLabelFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setLabelFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setLabelFontSize(Length fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// FontStyle accessor from parent Shape
	public void setLabelFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

	// ID accessor from parent Shape
	public void setLabelID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// LineCap accessor from parent Shape
	public void setLabelLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setLabelLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setLabelStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelStrokeColor(strokeColor, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setLabelStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setLabelStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Text accessor from parent Shape
	public void setLabelText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelText(text, ROIIndex, shapeIndex);
			}
		}
	}

	// TheC accessor from parent Shape
	public void setLabelTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setLabelTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setLabelTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setLabelTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	// Visible accessor from parent Shape
	public void setLabelVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelVisible(visible, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLabelX(Double x, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelX(x, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLabelY(Double y, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLabelY(y, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// Laser property storage
	//
	// {u'LightSource': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	// AnnotationRef accessor from parent LightSource
	public void setLaserAnnotationRef(String annotation, int instrumentIndex, int lightSourceIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLaserAnnotationRef(annotation, instrumentIndex, lightSourceIndex, annotationRefIndex);
			}
		}
	}

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
	public void setLaserPower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setLaserRepetitionRate(Frequency repetitionRate, int instrumentIndex, int lightSourceIndex)
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

	public void setLaserWavelength(Length wavelength, int instrumentIndex, int lightSourceIndex)
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightEmittingDiodeAnnotationRef(annotation, instrumentIndex, lightSourceIndex, annotationRefIndex);
			}
		}
	}

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
	public void setLightEmittingDiodePower(Power power, int instrumentIndex, int lightSourceIndex)
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

	public void setLightPathAnnotationRef(String annotation, int imageIndex, int channelIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLightPathAnnotationRef(annotation, imageIndex, channelIndex, annotationRefIndex);
			}
		}
	}

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

	public void setChannelLightSourceSettingsWavelength(Length wavelength, int imageIndex, int channelIndex)
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

	public void setMicrobeamManipulationLightSourceSettingsWavelength(Length wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex)
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

	// AnnotationRef accessor from parent Shape
	public void setLineAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setLineFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setLineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setLineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setLineFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// FontStyle accessor from parent Shape
	public void setLineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

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

	// LineCap accessor from parent Shape
	public void setLineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setLineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setLineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineStrokeColor(strokeColor, ROIIndex, shapeIndex);
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
	public void setLineStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Text accessor from parent Shape
	public void setLineText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineText(text, ROIIndex, shapeIndex);
			}
		}
	}

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
	public void setLineTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setLineVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineVisible(visible, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineMarkerEnd(markerEnd, ROIIndex, shapeIndex);
			}
		}
	}

	public void setLineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLineMarkerStart(markerStart, ROIIndex, shapeIndex);
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

	public void setListAnnotationAnnotator(String annotator, int listAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setListAnnotationAnnotator(annotator, listAnnotationIndex);
			}
		}
	}

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

	public void setLongAnnotationAnnotator(String annotator, int longAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setLongAnnotationAnnotator(annotator, longAnnotationIndex);
			}
		}
	}

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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMapAnnotationAnnotationRef(annotation, mapAnnotationIndex, annotationRefIndex);
			}
		}
	}

	public void setMapAnnotationAnnotator(String annotator, int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMapAnnotationAnnotator(annotator, mapAnnotationIndex);
			}
		}
	}

	public void setMapAnnotationDescription(String description, int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMapAnnotationDescription(description, mapAnnotationIndex);
			}
		}
	}

	public void setMapAnnotationID(String id, int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMapAnnotationID(id, mapAnnotationIndex);
			}
		}
	}

	public void setMapAnnotationNamespace(String namespace, int mapAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMapAnnotationNamespace(namespace, mapAnnotationIndex);
			}
		}
	}

	//
	// Mask property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setMaskAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setMaskFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setMaskFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setMaskFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setMaskFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// FontStyle accessor from parent Shape
	public void setMaskFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

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

	// LineCap accessor from parent Shape
	public void setMaskLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setMaskLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setMaskStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskStrokeColor(strokeColor, ROIIndex, shapeIndex);
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
	public void setMaskStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Text accessor from parent Shape
	public void setMaskText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskText(text, ROIIndex, shapeIndex);
			}
		}
	}

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
	public void setMaskTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setMaskVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setMaskVisible(visible, ROIIndex, shapeIndex);
			}
		}
	}

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
	// Objective property storage
	//
	// {u'Instrument': {u'OME': None}}
	// Is multi path? False

	public void setObjectiveAnnotationRef(String annotation, int instrumentIndex, int objectiveIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveAnnotationRef(annotation, instrumentIndex, objectiveIndex, annotationRefIndex);
			}
		}
	}

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

	public void setObjectiveNominalMagnification(Double nominalMagnification, int instrumentIndex, int objectiveIndex)
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

	public void setObjectiveWorkingDistance(Length workingDistance, int instrumentIndex, int objectiveIndex)
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
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setObjectiveSettingsCorrectionCollar(Double correctionCollar, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex);
			}
		}
	}

	public void setObjectiveSettingsID(String id, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveSettingsID(id, imageIndex);
			}
		}
	}

	public void setObjectiveSettingsMedium(Medium medium, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveSettingsMedium(medium, imageIndex);
			}
		}
	}

	public void setObjectiveSettingsRefractiveIndex(Double refractiveIndex, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex);
			}
		}
	}

	//
	// Pixels property storage
	//
	// {u'Image': {u'OME': None}}
	// Is multi path? False

	public void setPixelsBigEndian(Boolean bigEndian, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsBigEndian(bigEndian, imageIndex);
			}
		}
	}

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

	public void setPixelsInterleaved(Boolean interleaved, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsInterleaved(interleaved, imageIndex);
			}
		}
	}

	public void setPixelsPhysicalSizeX(Length physicalSizeX, int imageIndex)
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

	public void setPixelsPhysicalSizeY(Length physicalSizeY, int imageIndex)
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

	public void setPixelsPhysicalSizeZ(Length physicalSizeZ, int imageIndex)
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

	public void setPixelsSignificantBits(PositiveInteger significantBits, int imageIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPixelsSignificantBits(significantBits, imageIndex);
			}
		}
	}

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

	public void setPixelsTimeIncrement(Time timeIncrement, int imageIndex)
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

	public void setPlaneDeltaT(Time deltaT, int imageIndex, int planeIndex)
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

	public void setPlaneExposureTime(Time exposureTime, int imageIndex, int planeIndex)
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

	public void setPlanePositionX(Length positionX, int imageIndex, int planeIndex)
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

	public void setPlanePositionY(Length positionY, int imageIndex, int planeIndex)
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

	public void setPlanePositionZ(Length positionZ, int imageIndex, int planeIndex)
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

	public void setPlateFieldIndex(NonNegativeInteger fieldIndex, int plateIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPlateFieldIndex(fieldIndex, plateIndex);
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

	public void setPlateWellOriginX(Length wellOriginX, int plateIndex)
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

	public void setPlateWellOriginY(Length wellOriginY, int plateIndex)
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

	public void setPlateAcquisitionEndTime(Timestamp endTime, int plateIndex, int plateAcquisitionIndex)
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

	public void setPlateAcquisitionStartTime(Timestamp startTime, int plateIndex, int plateAcquisitionIndex)
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

	//
	// Point property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setPointAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setPointFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setPointFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setPointFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setPointFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// FontStyle accessor from parent Shape
	public void setPointFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

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

	// LineCap accessor from parent Shape
	public void setPointLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setPointLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setPointStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointStrokeColor(strokeColor, ROIIndex, shapeIndex);
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
	public void setPointStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Text accessor from parent Shape
	public void setPointText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointText(text, ROIIndex, shapeIndex);
			}
		}
	}

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
	public void setPointTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setPointVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPointVisible(visible, ROIIndex, shapeIndex);
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
	// Polygon property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setPolygonAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setPolygonFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setPolygonFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setPolygonFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setPolygonFontSize(Length fontSize, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonFontSize(fontSize, ROIIndex, shapeIndex);
			}
		}
	}

	// FontStyle accessor from parent Shape
	public void setPolygonFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

	// ID accessor from parent Shape
	public void setPolygonID(String id, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonID(id, ROIIndex, shapeIndex);
			}
		}
	}

	// LineCap accessor from parent Shape
	public void setPolygonLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setPolygonLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setPolygonStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonStrokeColor(strokeColor, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeDashArray accessor from parent Shape
	public void setPolygonStrokeDashArray(String strokeDashArray, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonStrokeDashArray(strokeDashArray, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeWidth accessor from parent Shape
	public void setPolygonStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonStrokeWidth(strokeWidth, ROIIndex, shapeIndex);
			}
		}
	}

	// Text accessor from parent Shape
	public void setPolygonText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonText(text, ROIIndex, shapeIndex);
			}
		}
	}

	// TheC accessor from parent Shape
	public void setPolygonTheC(NonNegativeInteger theC, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonTheC(theC, ROIIndex, shapeIndex);
			}
		}
	}

	// TheT accessor from parent Shape
	public void setPolygonTheT(NonNegativeInteger theT, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonTheT(theT, ROIIndex, shapeIndex);
			}
		}
	}

	// TheZ accessor from parent Shape
	public void setPolygonTheZ(NonNegativeInteger theZ, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonTheZ(theZ, ROIIndex, shapeIndex);
			}
		}
	}

	// Transform accessor from parent Shape
	public void setPolygonTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonTransform(transform, ROIIndex, shapeIndex);
			}
		}
	}

	// Visible accessor from parent Shape
	public void setPolygonVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonVisible(visible, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPolygonPoints(String points, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolygonPoints(points, ROIIndex, shapeIndex);
			}
		}
	}

	//
	// Polyline property storage
	//
	// {u'Shape': {u'Union': {u'ROI': {u'OME': None}}}}
	// Is multi path? False

	// AnnotationRef accessor from parent Shape
	public void setPolylineAnnotationRef(String annotation, int ROIIndex, int shapeIndex, int annotationRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setPolylineFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setPolylineFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setPolylineFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setPolylineFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// FontStyle accessor from parent Shape
	public void setPolylineFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

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

	// LineCap accessor from parent Shape
	public void setPolylineLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setPolylineLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setPolylineStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineStrokeColor(strokeColor, ROIIndex, shapeIndex);
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
	public void setPolylineStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Text accessor from parent Shape
	public void setPolylineText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineText(text, ROIIndex, shapeIndex);
			}
		}
	}

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
	public void setPolylineTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setPolylineVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineVisible(visible, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPolylineMarkerEnd(Marker markerEnd, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineMarkerEnd(markerEnd, ROIIndex, shapeIndex);
			}
		}
	}

	public void setPolylineMarkerStart(Marker markerStart, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setPolylineMarkerStart(markerStart, ROIIndex, shapeIndex);
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

	public void setProjectDatasetRef(String dataset, int projectIndex, int datasetRefIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectDatasetRef(dataset, projectIndex, datasetRefIndex);
			}
		}
	}

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

	public void setProjectExperimenterGroupRef(String experimenterGroup, int projectIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setProjectExperimenterGroupRef(experimenterGroup, projectIndex);
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
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleAnnotationRef(annotation, ROIIndex, shapeIndex, annotationRefIndex);
			}
		}
	}

	// FillColor accessor from parent Shape
	public void setRectangleFillColor(Color fillColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleFillColor(fillColor, ROIIndex, shapeIndex);
			}
		}
	}

	// FillRule accessor from parent Shape
	public void setRectangleFillRule(FillRule fillRule, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleFillRule(fillRule, ROIIndex, shapeIndex);
			}
		}
	}

	// FontFamily accessor from parent Shape
	public void setRectangleFontFamily(FontFamily fontFamily, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleFontFamily(fontFamily, ROIIndex, shapeIndex);
			}
		}
	}

	// FontSize accessor from parent Shape
	public void setRectangleFontSize(Length fontSize, int ROIIndex, int shapeIndex)
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

	// FontStyle accessor from parent Shape
	public void setRectangleFontStyle(FontStyle fontStyle, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleFontStyle(fontStyle, ROIIndex, shapeIndex);
			}
		}
	}

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

	// LineCap accessor from parent Shape
	public void setRectangleLineCap(LineCap lineCap, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleLineCap(lineCap, ROIIndex, shapeIndex);
			}
		}
	}

	// Locked accessor from parent Shape
	public void setRectangleLocked(Boolean locked, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleLocked(locked, ROIIndex, shapeIndex);
			}
		}
	}

	// StrokeColor accessor from parent Shape
	public void setRectangleStrokeColor(Color strokeColor, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleStrokeColor(strokeColor, ROIIndex, shapeIndex);
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
	public void setRectangleStrokeWidth(Length strokeWidth, int ROIIndex, int shapeIndex)
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

	// Text accessor from parent Shape
	public void setRectangleText(String text, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleText(text, ROIIndex, shapeIndex);
			}
		}
	}

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
	public void setRectangleTransform(AffineTransform transform, int ROIIndex, int shapeIndex)
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

	// Visible accessor from parent Shape
	public void setRectangleVisible(Boolean visible, int ROIIndex, int shapeIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRectangleVisible(visible, ROIIndex, shapeIndex);
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
	// Rights property storage
	//
	// {u'OME': None}
	// Is multi path? False

	public void setRightsRightsHeld(String rightsHeld)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRightsRightsHeld(rightsHeld);
			}
		}
	}

	public void setRightsRightsHolder(String rightsHolder)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setRightsRightsHolder(rightsHolder);
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

	public void setStageLabelX(Length x, int imageIndex)
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

	public void setStageLabelY(Length y, int imageIndex)
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

	public void setStageLabelZ(Length z, int imageIndex)
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

	public void setTagAnnotationAnnotator(String annotator, int tagAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTagAnnotationAnnotator(annotator, tagAnnotationIndex);
			}
		}
	}

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

	public void setTermAnnotationAnnotator(String annotator, int termAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTermAnnotationAnnotator(annotator, termAnnotationIndex);
			}
		}
	}

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

	public void setTimestampAnnotationAnnotator(String annotator, int timestampAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setTimestampAnnotationAnnotator(annotator, timestampAnnotationIndex);
			}
		}
	}

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

	public void setTimestampAnnotationValue(Timestamp value, int timestampAnnotationIndex)
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

	//
	// TransmittanceRange property storage
	//
	// {u'Filter': {u'Instrument': {u'OME': None}}}
	// Is multi path? False

	public void setTransmittanceRangeCutIn(Length cutIn, int instrumentIndex, int filterIndex)
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

	public void setTransmittanceRangeCutInTolerance(Length cutInTolerance, int instrumentIndex, int filterIndex)
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

	public void setTransmittanceRangeCutOut(Length cutOut, int instrumentIndex, int filterIndex)
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

	public void setTransmittanceRangeCutOutTolerance(Length cutOutTolerance, int instrumentIndex, int filterIndex)
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

	public void setWellColor(Color color, int plateIndex, int wellIndex)
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

	public void setWellType(String type, int plateIndex, int wellIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setWellType(type, plateIndex, wellIndex);
			}
		}
	}

	//
	// WellSample property storage
	//
	// {u'Well': {u'Plate': {u'OME': None}}}
	// Is multi path? False

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

	public void setWellSamplePositionX(Length positionX, int plateIndex, int wellIndex, int wellSampleIndex)
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

	public void setWellSamplePositionY(Length positionY, int plateIndex, int wellIndex, int wellSampleIndex)
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

	public void setWellSampleTimepoint(Timestamp timepoint, int plateIndex, int wellIndex, int wellSampleIndex)
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

	public void setXMLAnnotationAnnotator(String annotator, int XMLAnnotationIndex)
	{
		for (Iterator iter = delegates.iterator(); iter.hasNext();)
		{
			Object o = iter.next();
			if (o instanceof MetadataStore)
			{
				MetadataStore store = (MetadataStore) o;
				store.setXMLAnnotationAnnotator(annotator, XMLAnnotationIndex);
			}
		}
	}

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

}
