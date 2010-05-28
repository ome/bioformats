//
// AggregateMetadata.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2010 UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by melissa via MetadataAutogen on May 10, 2010 9:27:03 PM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import java.util.Iterator;
import java.util.List;

import loci.common.DataTools;

import ome.xml.r201004.enums.*;
import ome.xml.r201004.primitives.*;

/**
 * A metadata store which delegates the actual storage to one or more <i>sub</i>
 * metadata stores.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/meta/AggregateMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/meta/AggregateMetadata.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AggregateMetadata implements IMetadata {

  // -- Fields --

  /** The active metadata store delegates. */
  private List delegates;

  // -- Constructor --

  /**
   * Creates a new instance.
   * @param delegates of type {@link MetadataRetrieve}
   *   and/or {@link MetadataStore}.
   */
  public AggregateMetadata(List delegates) {
    this.delegates = delegates;
  }

  // -- AggregateMetadata API methods --

  /**
   * Adds a delegate to the metadata store.
   * @param delegate a <code>MetadataStore</code>
   */
  public void addDelegate(MetadataStore delegate) {
    delegates.add(delegate);
  }

  /**
   * Removes a delegate from the metadata store.
   * @param delegate a <code>MetadataStore</code>
   */
  public void removeDelegate(MetadataStore delegate) {
    delegates.remove(delegate);
  }

  /**
   * Retrieves the current list of metadata store delegates.
   * @return list of {@link MetadataStore} delegates.
   */
  public List getDelegates() {
    return delegates;
  }

  // -- MetadataRetrieve API methods --

  // - Entity counting -

  /* @see MetadataRetrieve#getBooleanAnnotationCount() */
  public int getBooleanAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getBooleanAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getChannelCount(int) */
  public int getChannelCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getChannelCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getChannelAnnotationRefCount(int, int) */
  public int getChannelAnnotationRefCount(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getChannelAnnotationRefCount(imageIndex, channelIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getDatasetCount() */
  public int getDatasetCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getDatasetCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getDatasetAnnotationRefCount(int) */
  public int getDatasetAnnotationRefCount(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getDatasetAnnotationRefCount(datasetIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getDatasetRefCount(int) */
  public int getDatasetRefCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getDatasetRefCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getDetectorCount(int) */
  public int getDetectorCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getDetectorCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getDichroicCount(int) */
  public int getDichroicCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getDichroicCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getDoubleAnnotationCount() */
  public int getDoubleAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getDoubleAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getExperimentCount() */
  public int getExperimentCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getExperimentCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterCount() */
  public int getExperimenterCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getExperimenterCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterAnnotationRefCount(int) */
  public int getExperimenterAnnotationRefCount(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getExperimenterAnnotationRefCount(experimenterIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getExperimenterGroupRefCount(int) */
  public int getExperimenterGroupRefCount(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getExperimenterGroupRefCount(experimenterIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getFileAnnotationCount() */
  public int getFileAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getFileAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getFilterCount(int) */
  public int getFilterCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getFilterCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getFilterSetCount(int) */
  public int getFilterSetCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getFilterSetCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getFilterSetEmissionFilterRefCount(int, int) */
  public int getFilterSetEmissionFilterRefCount(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getFilterSetEmissionFilterRefCount(instrumentIndex, filterSetIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getFilterSetExcitationFilterRefCount(int, int) */
  public int getFilterSetExcitationFilterRefCount(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getFilterSetExcitationFilterRefCount(instrumentIndex, filterSetIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getGroupCount() */
  public int getGroupCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getGroupCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getImageCount() */
  public int getImageCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getImageCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getImageAnnotationRefCount(int) */
  public int getImageAnnotationRefCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getImageAnnotationRefCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getImageROIRefCount(int) */
  public int getImageROIRefCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getImageROIRefCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getInstrumentCount() */
  public int getInstrumentCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getInstrumentCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getLightPathEmissionFilterRefCount(int, int) */
  public int getLightPathEmissionFilterRefCount(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getLightPathEmissionFilterRefCount(imageIndex, channelIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getLightPathExcitationFilterRefCount(int, int) */
  public int getLightPathExcitationFilterRefCount(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getLightPathExcitationFilterRefCount(imageIndex, channelIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getListAnnotationCount() */
  public int getListAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getListAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getListAnnotationAnnotationRefCount(int) */
  public int getListAnnotationAnnotationRefCount(int listAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getListAnnotationAnnotationRefCount(listAnnotationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getLongAnnotationCount() */
  public int getLongAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getLongAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationCount(int) */
  public int getMicrobeamManipulationCount(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getMicrobeamManipulationCount(experimentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsCount(int, int) */
  public int getMicrobeamManipulationLightSourceSettingsCount(int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getMicrobeamManipulationLightSourceSettingsCount(experimentIndex, microbeamManipulationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationROIRefCount(int, int) */
  public int getMicrobeamManipulationROIRefCount(int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getMicrobeamManipulationROIRefCount(experimentIndex, microbeamManipulationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationRefCount(int) */
  public int getMicrobeamManipulationRefCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getMicrobeamManipulationRefCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getOTFCount(int) */
  public int getOTFCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getOTFCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getObjectiveCount(int) */
  public int getObjectiveCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getObjectiveCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPixelsAnnotationRefCount(int) */
  public int getPixelsAnnotationRefCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPixelsAnnotationRefCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlaneCount(int) */
  public int getPlaneCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlaneCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlaneAnnotationRefCount(int, int) */
  public int getPlaneAnnotationRefCount(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlaneAnnotationRefCount(imageIndex, planeIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlateCount() */
  public int getPlateCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlateCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionCount(int) */
  public int getPlateAcquisitionCount(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlateAcquisitionCount(plateIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionAnnotationRefCount(int, int) */
  public int getPlateAcquisitionAnnotationRefCount(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlateAcquisitionAnnotationRefCount(plateIndex, plateAcquisitionIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlateAnnotationRefCount(int) */
  public int getPlateAnnotationRefCount(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlateAnnotationRefCount(plateIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlateRefCount(int) */
  public int getPlateRefCount(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlateRefCount(screenIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getProjectCount() */
  public int getProjectCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getProjectCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getProjectAnnotationRefCount(int) */
  public int getProjectAnnotationRefCount(int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getProjectAnnotationRefCount(projectIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getProjectRefCount(int) */
  public int getProjectRefCount(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getProjectRefCount(datasetIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getROICount() */
  public int getROICount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getROICount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getROIAnnotationRefCount(int) */
  public int getROIAnnotationRefCount(int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getROIAnnotationRefCount(roiIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getReagentCount(int) */
  public int getReagentCount(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getReagentCount(screenIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getReagentAnnotationRefCount(int, int) */
  public int getReagentAnnotationRefCount(int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getReagentAnnotationRefCount(screenIndex, reagentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getScreenCount() */
  public int getScreenCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getScreenCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getScreenAnnotationRefCount(int) */
  public int getScreenAnnotationRefCount(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getScreenAnnotationRefCount(screenIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getScreenRefCount(int) */
  public int getScreenRefCount(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getScreenRefCount(plateIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getShapeAnnotationRefCount(int, int) */
  public int getShapeAnnotationRefCount(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getShapeAnnotationRefCount(roiIndex, shapeIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getStringAnnotationCount() */
  public int getStringAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getStringAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getTiffDataCount(int) */
  public int getTiffDataCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getTiffDataCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getTimestampAnnotationCount() */
  public int getTimestampAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getTimestampAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getWellCount(int) */
  public int getWellCount(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getWellCount(plateIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getWellAnnotationRefCount(int, int) */
  public int getWellAnnotationRefCount(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getWellAnnotationRefCount(plateIndex, wellIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getWellSampleCount(int, int) */
  public int getWellSampleCount(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getWellSampleCount(plateIndex, wellIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getWellSampleAnnotationRefCount(int, int, int) */
  public int getWellSampleAnnotationRefCount(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getWellSampleAnnotationRefCount(plateIndex, wellIndex, wellSampleIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getWellSampleRefCount(int, int) */
  public int getWellSampleRefCount(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getWellSampleRefCount(plateIndex, plateAcquisitionIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getXMLAnnotationCount() */
  public int getXMLAnnotationCount() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getXMLAnnotationCount();
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  // - Entity retrieval -

  /* @see MetadataRetrieve#getUUID() */
  public String getUUID() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getUUID();
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Arc property retrieval -

  /* @see MetadataRetrieve#getArcID(int, int) */
  public String getArcID(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getArcID(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArcLotNumber(int, int) */
  public String getArcLotNumber(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getArcLotNumber(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArcManufacturer(int, int) */
  public String getArcManufacturer(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getArcManufacturer(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArcModel(int, int) */
  public String getArcModel(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getArcModel(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArcPower(int, int) */
  public Double getArcPower(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getArcPower(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArcSerialNumber(int, int) */
  public String getArcSerialNumber(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getArcSerialNumber(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArcType(int, int) */
  public ArcType getArcType(int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        ArcType result = retrieve.getArcType(instrumentIndex, arcIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - BooleanAnnotation property retrieval -

  /* @see MetadataRetrieve#getBooleanAnnotationID(int) */
  public String getBooleanAnnotationID(int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getBooleanAnnotationID(booleanAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getBooleanAnnotationNamespace(int) */
  public String getBooleanAnnotationNamespace(int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getBooleanAnnotationNamespace(booleanAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getBooleanAnnotationValue(int) */
  public Boolean getBooleanAnnotationValue(int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getBooleanAnnotationValue(booleanAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Channel property retrieval -

  /* @see MetadataRetrieve#getChannelAcquisitionMode(int, int) */
  public AcquisitionMode getChannelAcquisitionMode(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        AcquisitionMode result = retrieve.getChannelAcquisitionMode(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelAnnotationRef(int, int, int) */
  public String getChannelAnnotationRef(int imageIndex, int channelIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelAnnotationRef(imageIndex, channelIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelColor(int, int) */
  public Integer getChannelColor(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getChannelColor(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelContrastMethod(int, int) */
  public ContrastMethod getChannelContrastMethod(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        ContrastMethod result = retrieve.getChannelContrastMethod(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelEmissionWavelength(int, int) */
  public PositiveInteger getChannelEmissionWavelength(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getChannelEmissionWavelength(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelExcitationWavelength(int, int) */
  public PositiveInteger getChannelExcitationWavelength(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getChannelExcitationWavelength(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelFilterSetRef(int, int) */
  public String getChannelFilterSetRef(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelFilterSetRef(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelFluor(int, int) */
  public String getChannelFluor(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelFluor(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelID(int, int) */
  public String getChannelID(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelID(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelIlluminationType(int, int) */
  public IlluminationType getChannelIlluminationType(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        IlluminationType result = retrieve.getChannelIlluminationType(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelLightSourceSettingsAttenuation(int, int) */
  public PercentFraction getChannelLightSourceSettingsAttenuation(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PercentFraction result = retrieve.getChannelLightSourceSettingsAttenuation(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelLightSourceSettingsID(int, int) */
  public String getChannelLightSourceSettingsID(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelLightSourceSettingsID(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelLightSourceSettingsWavelength(int, int) */
  public PositiveInteger getChannelLightSourceSettingsWavelength(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getChannelLightSourceSettingsWavelength(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelNDFilter(int, int) */
  public Double getChannelNDFilter(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getChannelNDFilter(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelName(int, int) */
  public String getChannelName(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelName(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelOTFRef(int, int) */
  public String getChannelOTFRef(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelOTFRef(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelPinholeSize(int, int) */
  public Double getChannelPinholeSize(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getChannelPinholeSize(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelPockelCellSetting(int, int) */
  public Integer getChannelPockelCellSetting(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getChannelPockelCellSetting(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelSamplesPerPixel(int, int) */
  public Integer getChannelSamplesPerPixel(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getChannelSamplesPerPixel(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ChannelAnnotationRef property retrieval -

  // - Dataset property retrieval -

  /* @see MetadataRetrieve#getDatasetAnnotationRef(int, int) */
  public String getDatasetAnnotationRef(int datasetIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetAnnotationRef(datasetIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDatasetDescription(int) */
  public String getDatasetDescription(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetDescription(datasetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDatasetExperimenterRef(int) */
  public String getDatasetExperimenterRef(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetExperimenterRef(datasetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDatasetGroupRef(int) */
  public String getDatasetGroupRef(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetGroupRef(datasetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDatasetID(int) */
  public String getDatasetID(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetID(datasetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDatasetName(int) */
  public String getDatasetName(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetName(datasetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDatasetProjectRef(int, int) */
  public String getDatasetProjectRef(int datasetIndex, int projectRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetProjectRef(datasetIndex, projectRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - DatasetAnnotationRef property retrieval -

  // - DatasetRef property retrieval -

  // - Detector property retrieval -

  /* @see MetadataRetrieve#getDetectorAmplificationGain(int, int) */
  public Double getDetectorAmplificationGain(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorGain(int, int) */
  public Double getDetectorGain(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorGain(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorID(int, int) */
  public String getDetectorID(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorID(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorLotNumber(int, int) */
  public String getDetectorLotNumber(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorLotNumber(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorManufacturer(int, int) */
  public String getDetectorManufacturer(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorManufacturer(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorModel(int, int) */
  public String getDetectorModel(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorModel(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorOffset(int, int) */
  public Double getDetectorOffset(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorOffset(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSerialNumber(int, int) */
  public String getDetectorSerialNumber(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorSerialNumber(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorType(int, int) */
  public DetectorType getDetectorType(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        DetectorType result = retrieve.getDetectorType(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorVoltage(int, int) */
  public Double getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorVoltage(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorZoom(int, int) */
  public Double getDetectorZoom(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorZoom(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - DetectorSettings property retrieval -

  /* @see MetadataRetrieve#getDetectorSettingsBinning(int, int) */
  public Binning getDetectorSettingsBinning(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Binning result = retrieve.getDetectorSettingsBinning(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Double getDetectorSettingsGain(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorSettingsGain(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsID(int, int) */
  public String getDetectorSettingsID(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorSettingsID(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Double getDetectorSettingsOffset(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorSettingsOffset(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsReadOutRate(int, int) */
  public Double getDetectorSettingsReadOutRate(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorSettingsReadOutRate(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsVoltage(int, int) */
  public Double getDetectorSettingsVoltage(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDetectorSettingsVoltage(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Dichroic property retrieval -

  /* @see MetadataRetrieve#getDichroicID(int, int) */
  public String getDichroicID(int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDichroicID(instrumentIndex, dichroicIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDichroicLotNumber(int, int) */
  public String getDichroicLotNumber(int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDichroicLotNumber(instrumentIndex, dichroicIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDichroicManufacturer(int, int) */
  public String getDichroicManufacturer(int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDichroicManufacturer(instrumentIndex, dichroicIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDichroicModel(int, int) */
  public String getDichroicModel(int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDichroicModel(instrumentIndex, dichroicIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDichroicSerialNumber(int, int) */
  public String getDichroicSerialNumber(int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDichroicSerialNumber(instrumentIndex, dichroicIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - DoubleAnnotation property retrieval -

  /* @see MetadataRetrieve#getDoubleAnnotationID(int) */
  public String getDoubleAnnotationID(int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDoubleAnnotationID(doubleAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDoubleAnnotationNamespace(int) */
  public String getDoubleAnnotationNamespace(int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDoubleAnnotationNamespace(doubleAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDoubleAnnotationValue(int) */
  public Double getDoubleAnnotationValue(int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getDoubleAnnotationValue(doubleAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Ellipse property retrieval -

  /* @see MetadataRetrieve#getEllipseDescription(int, int) */
  public String getEllipseDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseFill(int, int) */
  public Integer getEllipseFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getEllipseFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseFontSize(int, int) */
  public Integer getEllipseFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getEllipseFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseID(int, int) */
  public String getEllipseID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseLabel(int, int) */
  public String getEllipseLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseName(int, int) */
  public String getEllipseName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseRadiusX(int, int) */
  public Double getEllipseRadiusX(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getEllipseRadiusX(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseRadiusY(int, int) */
  public Double getEllipseRadiusY(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getEllipseRadiusY(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseStroke(int, int) */
  public Integer getEllipseStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getEllipseStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseStrokeDashArray(int, int) */
  public String getEllipseStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseStrokeWidth(int, int) */
  public Double getEllipseStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getEllipseStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseTheC(int, int) */
  public Integer getEllipseTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getEllipseTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseTheT(int, int) */
  public Integer getEllipseTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getEllipseTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseTheZ(int, int) */
  public Integer getEllipseTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getEllipseTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseTransform(int, int) */
  public String getEllipseTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseX(int, int) */
  public Double getEllipseX(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getEllipseX(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseY(int, int) */
  public Double getEllipseY(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getEllipseY(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Experiment property retrieval -

  /* @see MetadataRetrieve#getExperimentDescription(int) */
  public String getExperimentDescription(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimentDescription(experimentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimentExperimenterRef(int) */
  public String getExperimentExperimenterRef(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimentExperimenterRef(experimentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimentID(int) */
  public String getExperimentID(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimentID(experimentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimentType(int) */
  public ExperimentType getExperimentType(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        ExperimentType result = retrieve.getExperimentType(experimentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Experimenter property retrieval -

  /* @see MetadataRetrieve#getExperimenterAnnotationRef(int, int) */
  public String getExperimenterAnnotationRef(int experimenterIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterAnnotationRef(experimenterIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterDisplayName(int) */
  public String getExperimenterDisplayName(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterDisplayName(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterEmail(int) */
  public String getExperimenterEmail(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterEmail(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterFirstName(int) */
  public String getExperimenterFirstName(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterFirstName(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterGroupRef(int, int) */
  public String getExperimenterGroupRef(int experimenterIndex, int groupRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterGroupRef(experimenterIndex, groupRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterID(int) */
  public String getExperimenterID(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterID(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterInstitution(int) */
  public String getExperimenterInstitution(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterInstitution(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterLastName(int) */
  public String getExperimenterLastName(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterLastName(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterMiddleName(int) */
  public String getExperimenterMiddleName(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterMiddleName(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenterUserName(int) */
  public String getExperimenterUserName(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterUserName(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ExperimenterAnnotationRef property retrieval -

  // - ExperimenterGroupRef property retrieval -

  // - Filament property retrieval -

  /* @see MetadataRetrieve#getFilamentID(int, int) */
  public String getFilamentID(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilamentID(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilamentLotNumber(int, int) */
  public String getFilamentLotNumber(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilamentLotNumber(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilamentManufacturer(int, int) */
  public String getFilamentManufacturer(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilamentManufacturer(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilamentModel(int, int) */
  public String getFilamentModel(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilamentModel(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilamentPower(int, int) */
  public Double getFilamentPower(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getFilamentPower(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilamentSerialNumber(int, int) */
  public String getFilamentSerialNumber(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilamentSerialNumber(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilamentType(int, int) */
  public FilamentType getFilamentType(int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        FilamentType result = retrieve.getFilamentType(instrumentIndex, filamentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - FileAnnotation property retrieval -

  /* @see MetadataRetrieve#getFileAnnotationBinaryFileFileName(int) */
  public String getFileAnnotationBinaryFileFileName(int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFileAnnotationBinaryFileFileName(fileAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFileAnnotationBinaryFileMIMEType(int) */
  public String getFileAnnotationBinaryFileMIMEType(int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFileAnnotationBinaryFileMIMEType(fileAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFileAnnotationBinaryFileSize(int) */
  public Integer getFileAnnotationBinaryFileSize(int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getFileAnnotationBinaryFileSize(fileAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFileAnnotationID(int) */
  public String getFileAnnotationID(int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFileAnnotationID(fileAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFileAnnotationNamespace(int) */
  public String getFileAnnotationNamespace(int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFileAnnotationNamespace(fileAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Filter property retrieval -

  /* @see MetadataRetrieve#getFilterFilterWheel(int, int) */
  public String getFilterFilterWheel(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterFilterWheel(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterID(int, int) */
  public String getFilterID(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterID(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterLotNumber(int, int) */
  public String getFilterLotNumber(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterLotNumber(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterManufacturer(int, int) */
  public String getFilterManufacturer(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterManufacturer(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterModel(int, int) */
  public String getFilterModel(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterModel(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSerialNumber(int, int) */
  public String getFilterSerialNumber(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSerialNumber(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterType(int, int) */
  public FilterType getFilterType(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        FilterType result = retrieve.getFilterType(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - FilterSet property retrieval -

  /* @see MetadataRetrieve#getFilterSetDichroicRef(int, int) */
  public String getFilterSetDichroicRef(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetDichroicRef(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetEmissionFilterRef(int, int, int) */
  public String getFilterSetEmissionFilterRef(int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetEmissionFilterRef(instrumentIndex, filterSetIndex, emissionFilterRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetExcitationFilterRef(int, int, int) */
  public String getFilterSetExcitationFilterRef(int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetExcitationFilterRef(instrumentIndex, filterSetIndex, excitationFilterRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetID(int, int) */
  public String getFilterSetID(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetID(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetLotNumber(int, int) */
  public String getFilterSetLotNumber(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetLotNumber(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetManufacturer(int, int) */
  public String getFilterSetManufacturer(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetManufacturer(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetModel(int, int) */
  public String getFilterSetModel(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetModel(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetSerialNumber(int, int) */
  public String getFilterSetSerialNumber(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetSerialNumber(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - FilterSetEmissionFilterRef property retrieval -

  // - FilterSetExcitationFilterRef property retrieval -

  // - Group property retrieval -

  /* @see MetadataRetrieve#getGroupContact(int) */
  public String getGroupContact(int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getGroupContact(groupIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getGroupDescription(int) */
  public String getGroupDescription(int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getGroupDescription(groupIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getGroupID(int) */
  public String getGroupID(int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getGroupID(groupIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getGroupLeader(int) */
  public String getGroupLeader(int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getGroupLeader(groupIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getGroupName(int) */
  public String getGroupName(int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getGroupName(groupIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Image property retrieval -

  /* @see MetadataRetrieve#getImageAcquiredDate(int) */
  public String getImageAcquiredDate(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageAcquiredDate(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageAnnotationRef(int, int) */
  public String getImageAnnotationRef(int imageIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageAnnotationRef(imageIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageDatasetRef(int, int) */
  public String getImageDatasetRef(int imageIndex, int datasetRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageDatasetRef(imageIndex, datasetRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageDescription(int) */
  public String getImageDescription(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageDescription(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageExperimentRef(int) */
  public String getImageExperimentRef(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageExperimentRef(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageExperimenterRef(int) */
  public String getImageExperimenterRef(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageExperimenterRef(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageGroupRef(int) */
  public String getImageGroupRef(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageGroupRef(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageID(int) */
  public String getImageID(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageID(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageInstrumentRef(int) */
  public String getImageInstrumentRef(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageInstrumentRef(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageMicrobeamManipulationRef(int, int) */
  public String getImageMicrobeamManipulationRef(int imageIndex, int microbeamManipulationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageMicrobeamManipulationRef(imageIndex, microbeamManipulationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageName(int) */
  public String getImageName(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageName(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageObjectiveSettingsCorrectionCollar(int) */
  public Double getImageObjectiveSettingsCorrectionCollar(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getImageObjectiveSettingsCorrectionCollar(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageObjectiveSettingsID(int) */
  public String getImageObjectiveSettingsID(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageObjectiveSettingsID(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageObjectiveSettingsMedium(int) */
  public Medium getImageObjectiveSettingsMedium(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Medium result = retrieve.getImageObjectiveSettingsMedium(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageObjectiveSettingsRefractiveIndex(int) */
  public Double getImageObjectiveSettingsRefractiveIndex(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getImageObjectiveSettingsRefractiveIndex(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageROIRef(int, int) */
  public String getImageROIRef(int imageIndex, int roiRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageROIRef(imageIndex, roiRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ImageAnnotationRef property retrieval -

  // - ImageROIRef property retrieval -

  // - ImagingEnvironment property retrieval -

  /* @see MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Double getImagingEnvironmentAirPressure(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getImagingEnvironmentAirPressure(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public PercentFraction getImagingEnvironmentCO2Percent(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PercentFraction result = retrieve.getImagingEnvironmentCO2Percent(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public PercentFraction getImagingEnvironmentHumidity(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PercentFraction result = retrieve.getImagingEnvironmentHumidity(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Double getImagingEnvironmentTemperature(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getImagingEnvironmentTemperature(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Instrument property retrieval -

  /* @see MetadataRetrieve#getInstrumentID(int) */
  public String getInstrumentID(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getInstrumentID(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Laser property retrieval -

  /* @see MetadataRetrieve#getLaserFrequencyMultiplication(int, int) */
  public PositiveInteger getLaserFrequencyMultiplication(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getLaserFrequencyMultiplication(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserID(int, int) */
  public String getLaserID(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserID(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserLaserMedium(int, int) */
  public LaserMedium getLaserLaserMedium(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        LaserMedium result = retrieve.getLaserLaserMedium(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserLotNumber(int, int) */
  public String getLaserLotNumber(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserLotNumber(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserManufacturer(int, int) */
  public String getLaserManufacturer(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserManufacturer(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserModel(int, int) */
  public String getLaserModel(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserModel(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserPockelCell(int, int) */
  public Boolean getLaserPockelCell(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getLaserPockelCell(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserPower(int, int) */
  public Double getLaserPower(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLaserPower(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserPulse(int, int) */
  public Pulse getLaserPulse(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Pulse result = retrieve.getLaserPulse(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserPump(int, int) */
  public String getLaserPump(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserPump(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserRepetitionRate(int, int) */
  public Double getLaserRepetitionRate(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLaserRepetitionRate(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserSerialNumber(int, int) */
  public String getLaserSerialNumber(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserSerialNumber(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getLaserTuneable(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserType(int, int) */
  public LaserType getLaserType(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        LaserType result = retrieve.getLaserType(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserWavelength(int, int) */
  public PositiveInteger getLaserWavelength(int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getLaserWavelength(instrumentIndex, laserIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LightEmittingDiode property retrieval -

  /* @see MetadataRetrieve#getLightEmittingDiodeID(int, int) */
  public String getLightEmittingDiodeID(int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightEmittingDiodeID(instrumentIndex, lightEmittingDiodeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightEmittingDiodeLotNumber(int, int) */
  public String getLightEmittingDiodeLotNumber(int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightEmittingDiodeLotNumber(instrumentIndex, lightEmittingDiodeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightEmittingDiodeManufacturer(int, int) */
  public String getLightEmittingDiodeManufacturer(int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightEmittingDiodeManufacturer(instrumentIndex, lightEmittingDiodeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightEmittingDiodeModel(int, int) */
  public String getLightEmittingDiodeModel(int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightEmittingDiodeModel(instrumentIndex, lightEmittingDiodeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightEmittingDiodePower(int, int) */
  public Double getLightEmittingDiodePower(int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLightEmittingDiodePower(instrumentIndex, lightEmittingDiodeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightEmittingDiodeSerialNumber(int, int) */
  public String getLightEmittingDiodeSerialNumber(int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightEmittingDiodeSerialNumber(instrumentIndex, lightEmittingDiodeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LightPath property retrieval -

  /* @see MetadataRetrieve#getLightPathDichroicRef(int, int) */
  public String getLightPathDichroicRef(int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightPathDichroicRef(imageIndex, channelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightPathEmissionFilterRef(int, int, int) */
  public String getLightPathEmissionFilterRef(int imageIndex, int channelIndex, int emissionFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightPathEmissionFilterRef(imageIndex, channelIndex, emissionFilterRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightPathExcitationFilterRef(int, int, int) */
  public String getLightPathExcitationFilterRef(int imageIndex, int channelIndex, int excitationFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightPathExcitationFilterRef(imageIndex, channelIndex, excitationFilterRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LightPathEmissionFilterRef property retrieval -

  // - LightPathExcitationFilterRef property retrieval -

  // - Line property retrieval -

  /* @see MetadataRetrieve#getLineDescription(int, int) */
  public String getLineDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineFill(int, int) */
  public Integer getLineFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLineFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineFontSize(int, int) */
  public Integer getLineFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLineFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineID(int, int) */
  public String getLineID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineLabel(int, int) */
  public String getLineLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineName(int, int) */
  public String getLineName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineStroke(int, int) */
  public Integer getLineStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLineStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineStrokeDashArray(int, int) */
  public String getLineStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineStrokeWidth(int, int) */
  public Double getLineStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLineStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineTheC(int, int) */
  public Integer getLineTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLineTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineTheT(int, int) */
  public Integer getLineTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLineTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineTheZ(int, int) */
  public Integer getLineTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLineTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineTransform(int, int) */
  public String getLineTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineX1(int, int) */
  public Double getLineX1(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLineX1(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineX2(int, int) */
  public Double getLineX2(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLineX2(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineY1(int, int) */
  public Double getLineY1(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLineY1(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineY2(int, int) */
  public Double getLineY2(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getLineY2(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ListAnnotation property retrieval -

  /* @see MetadataRetrieve#getListAnnotationAnnotationRef(int, int) */
  public String getListAnnotationAnnotationRef(int listAnnotationIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getListAnnotationAnnotationRef(listAnnotationIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getListAnnotationID(int) */
  public String getListAnnotationID(int listAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getListAnnotationID(listAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getListAnnotationNamespace(int) */
  public String getListAnnotationNamespace(int listAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getListAnnotationNamespace(listAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ListAnnotationAnnotationRef property retrieval -

  // - LongAnnotation property retrieval -

  /* @see MetadataRetrieve#getLongAnnotationID(int) */
  public String getLongAnnotationID(int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLongAnnotationID(longAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLongAnnotationNamespace(int) */
  public String getLongAnnotationNamespace(int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLongAnnotationNamespace(longAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLongAnnotationValue(int) */
  public Long getLongAnnotationValue(int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Long result = retrieve.getLongAnnotationValue(longAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Mask property retrieval -

  /* @see MetadataRetrieve#getMaskDescription(int, int) */
  public String getMaskDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskFill(int, int) */
  public Integer getMaskFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskFontSize(int, int) */
  public Integer getMaskFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskID(int, int) */
  public String getMaskID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskLabel(int, int) */
  public String getMaskLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskName(int, int) */
  public String getMaskName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskStroke(int, int) */
  public Integer getMaskStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskStrokeDashArray(int, int) */
  public String getMaskStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskStrokeWidth(int, int) */
  public Double getMaskStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getMaskStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskTheC(int, int) */
  public Integer getMaskTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskTheT(int, int) */
  public Integer getMaskTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskTheZ(int, int) */
  public Integer getMaskTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskTransform(int, int) */
  public String getMaskTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskX(int, int) */
  public Double getMaskX(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getMaskX(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskY(int, int) */
  public Double getMaskY(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getMaskY(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - MicrobeamManipulation property retrieval -

  /* @see MetadataRetrieve#getMicrobeamManipulationExperimenterRef(int, int) */
  public String getMicrobeamManipulationExperimenterRef(int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationExperimenterRef(experimentIndex, microbeamManipulationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationID(int, int) */
  public String getMicrobeamManipulationID(int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationID(experimentIndex, microbeamManipulationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationROIRef(int, int, int) */
  public String getMicrobeamManipulationROIRef(int experimentIndex, int microbeamManipulationIndex, int roiRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationROIRef(experimentIndex, microbeamManipulationIndex, roiRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationType(int, int) */
  public MicrobeamManipulationType getMicrobeamManipulationType(int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        MicrobeamManipulationType result = retrieve.getMicrobeamManipulationType(experimentIndex, microbeamManipulationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - MicrobeamManipulationLightSourceSettings property retrieval -

  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsAttenuation(int, int, int) */
  public PercentFraction getMicrobeamManipulationLightSourceSettingsAttenuation(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PercentFraction result = retrieve.getMicrobeamManipulationLightSourceSettingsAttenuation(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsID(int, int, int) */
  public String getMicrobeamManipulationLightSourceSettingsID(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationLightSourceSettingsID(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationLightSourceSettingsWavelength(int, int, int) */
  public PositiveInteger getMicrobeamManipulationLightSourceSettingsWavelength(int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getMicrobeamManipulationLightSourceSettingsWavelength(experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - MicrobeamManipulationROIRef property retrieval -

  // - MicrobeamManipulationRef property retrieval -

  // - Microscope property retrieval -

  /* @see MetadataRetrieve#getMicroscopeLotNumber(int) */
  public String getMicroscopeLotNumber(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicroscopeLotNumber(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicroscopeManufacturer(int) */
  public String getMicroscopeManufacturer(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicroscopeManufacturer(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicroscopeModel(int) */
  public String getMicroscopeModel(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicroscopeModel(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicroscopeSerialNumber(int) */
  public String getMicroscopeSerialNumber(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicroscopeSerialNumber(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicroscopeType(int) */
  public MicroscopeType getMicroscopeType(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        MicroscopeType result = retrieve.getMicroscopeType(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - OTF property retrieval -

  /* @see MetadataRetrieve#getOTFBinaryFileFileName(int, int) */
  public String getOTFBinaryFileFileName(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFBinaryFileFileName(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFBinaryFileMIMEType(int, int) */
  public String getOTFBinaryFileMIMEType(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFBinaryFileMIMEType(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFBinaryFileSize(int, int) */
  public Integer getOTFBinaryFileSize(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getOTFBinaryFileSize(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFFilterSetRef(int, int) */
  public String getOTFFilterSetRef(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFFilterSetRef(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFID(int, int) */
  public String getOTFID(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFID(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFObjectiveSettingsCorrectionCollar(int, int) */
  public Double getOTFObjectiveSettingsCorrectionCollar(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getOTFObjectiveSettingsCorrectionCollar(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFObjectiveSettingsID(int, int) */
  public String getOTFObjectiveSettingsID(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFObjectiveSettingsID(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFObjectiveSettingsMedium(int, int) */
  public Medium getOTFObjectiveSettingsMedium(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Medium result = retrieve.getOTFObjectiveSettingsMedium(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFObjectiveSettingsRefractiveIndex(int, int) */
  public Double getOTFObjectiveSettingsRefractiveIndex(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getOTFObjectiveSettingsRefractiveIndex(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFOpticalAxisAveraged(int, int) */
  public Boolean getOTFOpticalAxisAveraged(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getOTFOpticalAxisAveraged(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFSizeX(int, int) */
  public PositiveInteger getOTFSizeX(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getOTFSizeX(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFSizeY(int, int) */
  public PositiveInteger getOTFSizeY(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getOTFSizeY(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFType(int, int) */
  public PixelType getOTFType(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PixelType result = retrieve.getOTFType(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Objective property retrieval -

  /* @see MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Double getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveCorrection(int, int) */
  public Correction getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Correction result = retrieve.getObjectiveCorrection(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveID(int, int) */
  public String getObjectiveID(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveID(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveImmersion(int, int) */
  public Immersion getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Immersion result = retrieve.getObjectiveImmersion(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveIris(int, int) */
  public Boolean getObjectiveIris(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getObjectiveIris(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveLensNA(int, int) */
  public Double getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getObjectiveLensNA(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveLotNumber(int, int) */
  public String getObjectiveLotNumber(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveLotNumber(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveManufacturer(int, int) */
  public String getObjectiveManufacturer(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveManufacturer(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveModel(int, int) */
  public String getObjectiveModel(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveModel(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveNominalMagnification(int, int) */
  public Integer getObjectiveNominalMagnification(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getObjectiveNominalMagnification(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveSerialNumber(int, int) */
  public String getObjectiveSerialNumber(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveSerialNumber(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveWorkingDistance(int, int) */
  public Double getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Path property retrieval -

  /* @see MetadataRetrieve#getPathDefinition(int, int) */
  public String getPathDefinition(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathDefinition(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathDescription(int, int) */
  public String getPathDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathFill(int, int) */
  public Integer getPathFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPathFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathFontSize(int, int) */
  public Integer getPathFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPathFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathID(int, int) */
  public String getPathID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathLabel(int, int) */
  public String getPathLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathName(int, int) */
  public String getPathName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathStroke(int, int) */
  public Integer getPathStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPathStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathStrokeDashArray(int, int) */
  public String getPathStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathStrokeWidth(int, int) */
  public Double getPathStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPathStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathTheC(int, int) */
  public Integer getPathTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPathTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathTheT(int, int) */
  public Integer getPathTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPathTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathTheZ(int, int) */
  public Integer getPathTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPathTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathTransform(int, int) */
  public String getPathTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Pixels property retrieval -

  /* @see MetadataRetrieve#getPixelsAnnotationRef(int, int) */
  public String getPixelsAnnotationRef(int imageIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPixelsAnnotationRef(imageIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsDimensionOrder(int) */
  public DimensionOrder getPixelsDimensionOrder(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        DimensionOrder result = retrieve.getPixelsDimensionOrder(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsID(int) */
  public String getPixelsID(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPixelsID(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsPhysicalSizeX(int) */
  public Double getPixelsPhysicalSizeX(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPixelsPhysicalSizeX(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsPhysicalSizeY(int) */
  public Double getPixelsPhysicalSizeY(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPixelsPhysicalSizeY(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsPhysicalSizeZ(int) */
  public Double getPixelsPhysicalSizeZ(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPixelsPhysicalSizeZ(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeC(int) */
  public PositiveInteger getPixelsSizeC(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getPixelsSizeC(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeT(int) */
  public PositiveInteger getPixelsSizeT(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getPixelsSizeT(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeX(int) */
  public PositiveInteger getPixelsSizeX(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getPixelsSizeX(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeY(int) */
  public PositiveInteger getPixelsSizeY(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getPixelsSizeY(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeZ(int) */
  public PositiveInteger getPixelsSizeZ(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PositiveInteger result = retrieve.getPixelsSizeZ(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsTimeIncrement(int) */
  public Double getPixelsTimeIncrement(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPixelsTimeIncrement(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsType(int) */
  public PixelType getPixelsType(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PixelType result = retrieve.getPixelsType(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - PixelsAnnotationRef property retrieval -

  // - PixelsBinData property retrieval -

  /* @see MetadataRetrieve#getPixelsBinDataBigEndian(int, int) */
  public Boolean getPixelsBinDataBigEndian(int imageIndex, int binDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getPixelsBinDataBigEndian(imageIndex, binDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Plane property retrieval -

  /* @see MetadataRetrieve#getPlaneAnnotationRef(int, int, int) */
  public String getPlaneAnnotationRef(int imageIndex, int planeIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlaneAnnotationRef(imageIndex, planeIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneDeltaT(int, int) */
  public Double getPlaneDeltaT(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlaneDeltaT(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneExposureTime(int, int) */
  public Double getPlaneExposureTime(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlaneExposureTime(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneHashSHA1(int, int) */
  public String getPlaneHashSHA1(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlaneHashSHA1(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlanePositionX(int, int) */
  public Double getPlanePositionX(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlanePositionX(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlanePositionY(int, int) */
  public Double getPlanePositionY(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlanePositionY(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlanePositionZ(int, int) */
  public Double getPlanePositionZ(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlanePositionZ(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTheC(int, int) */
  public Integer getPlaneTheC(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlaneTheC(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTheT(int, int) */
  public Integer getPlaneTheT(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlaneTheT(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTheZ(int, int) */
  public Integer getPlaneTheZ(int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlaneTheZ(imageIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - PlaneAnnotationRef property retrieval -

  // - Plate property retrieval -

  /* @see MetadataRetrieve#getPlateAnnotationRef(int, int) */
  public String getPlateAnnotationRef(int plateIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAnnotationRef(plateIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateColumnNamingConvention(int) */
  public NamingConvention getPlateColumnNamingConvention(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        NamingConvention result = retrieve.getPlateColumnNamingConvention(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateColumns(int) */
  public Integer getPlateColumns(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlateColumns(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateDescription(int) */
  public String getPlateDescription(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateDescription(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateExternalIdentifier(int) */
  public String getPlateExternalIdentifier(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateExternalIdentifier(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateID(int) */
  public String getPlateID(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateID(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateName(int) */
  public String getPlateName(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateName(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateRowNamingConvention(int) */
  public NamingConvention getPlateRowNamingConvention(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        NamingConvention result = retrieve.getPlateRowNamingConvention(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateRows(int) */
  public Integer getPlateRows(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlateRows(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateScreenRef(int, int) */
  public String getPlateScreenRef(int plateIndex, int screenRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateScreenRef(plateIndex, screenRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateStatus(int) */
  public String getPlateStatus(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateStatus(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateWellOriginX(int) */
  public Double getPlateWellOriginX(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlateWellOriginX(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateWellOriginY(int) */
  public Double getPlateWellOriginY(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPlateWellOriginY(plateIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - PlateAcquisition property retrieval -

  /* @see MetadataRetrieve#getPlateAcquisitionAnnotationRef(int, int, int) */
  public String getPlateAcquisitionAnnotationRef(int plateIndex, int plateAcquisitionIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionAnnotationRef(plateIndex, plateAcquisitionIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionDescription(int, int) */
  public String getPlateAcquisitionDescription(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionDescription(plateIndex, plateAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionEndTime(int, int) */
  public String getPlateAcquisitionEndTime(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionEndTime(plateIndex, plateAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionID(int, int) */
  public String getPlateAcquisitionID(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionID(plateIndex, plateAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionMaximumFieldCount(int, int) */
  public Integer getPlateAcquisitionMaximumFieldCount(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlateAcquisitionMaximumFieldCount(plateIndex, plateAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionName(int, int) */
  public String getPlateAcquisitionName(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionName(plateIndex, plateAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionStartTime(int, int) */
  public String getPlateAcquisitionStartTime(int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionStartTime(plateIndex, plateAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateAcquisitionWellSampleRef(int, int, int) */
  public String getPlateAcquisitionWellSampleRef(int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateAcquisitionWellSampleRef(plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - PlateAcquisitionAnnotationRef property retrieval -

  // - PlateAnnotationRef property retrieval -

  // - PlateRef property retrieval -

  // - Point property retrieval -

  /* @see MetadataRetrieve#getPointDescription(int, int) */
  public String getPointDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointFill(int, int) */
  public Integer getPointFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPointFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointFontSize(int, int) */
  public Integer getPointFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPointFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointID(int, int) */
  public String getPointID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointLabel(int, int) */
  public String getPointLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointName(int, int) */
  public String getPointName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointStroke(int, int) */
  public Integer getPointStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPointStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointStrokeDashArray(int, int) */
  public String getPointStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointStrokeWidth(int, int) */
  public Double getPointStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPointStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointTheC(int, int) */
  public Integer getPointTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPointTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointTheT(int, int) */
  public Integer getPointTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPointTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointTheZ(int, int) */
  public Integer getPointTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPointTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointTransform(int, int) */
  public String getPointTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointX(int, int) */
  public Double getPointX(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPointX(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointY(int, int) */
  public Double getPointY(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPointY(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Polyline property retrieval -

  /* @see MetadataRetrieve#getPolylineClosed(int, int) */
  public Boolean getPolylineClosed(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getPolylineClosed(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineDescription(int, int) */
  public String getPolylineDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineFill(int, int) */
  public Integer getPolylineFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPolylineFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineFontSize(int, int) */
  public Integer getPolylineFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPolylineFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineID(int, int) */
  public String getPolylineID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineLabel(int, int) */
  public String getPolylineLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineName(int, int) */
  public String getPolylineName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylinePoints(int, int) */
  public String getPolylinePoints(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylinePoints(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineStroke(int, int) */
  public Integer getPolylineStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPolylineStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineStrokeDashArray(int, int) */
  public String getPolylineStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineStrokeWidth(int, int) */
  public Double getPolylineStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getPolylineStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineTheC(int, int) */
  public Integer getPolylineTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPolylineTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineTheT(int, int) */
  public Integer getPolylineTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPolylineTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineTheZ(int, int) */
  public Integer getPolylineTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPolylineTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineTransform(int, int) */
  public String getPolylineTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Project property retrieval -

  /* @see MetadataRetrieve#getProjectAnnotationRef(int, int) */
  public String getProjectAnnotationRef(int projectIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectAnnotationRef(projectIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getProjectDescription(int) */
  public String getProjectDescription(int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectDescription(projectIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getProjectExperimenterRef(int) */
  public String getProjectExperimenterRef(int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectExperimenterRef(projectIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getProjectGroupRef(int) */
  public String getProjectGroupRef(int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectGroupRef(projectIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getProjectID(int) */
  public String getProjectID(int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectID(projectIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getProjectName(int) */
  public String getProjectName(int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectName(projectIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ProjectAnnotationRef property retrieval -

  // - ProjectRef property retrieval -

  // - ROI property retrieval -

  /* @see MetadataRetrieve#getROIAnnotationRef(int, int) */
  public String getROIAnnotationRef(int roiIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROIAnnotationRef(roiIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIDescription(int) */
  public String getROIDescription(int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROIDescription(roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIID(int) */
  public String getROIID(int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROIID(roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIName(int) */
  public String getROIName(int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROIName(roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROINamespace(int) */
  public String getROINamespace(int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROINamespace(roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ROIAnnotationRef property retrieval -

  // - Reagent property retrieval -

  /* @see MetadataRetrieve#getReagentAnnotationRef(int, int, int) */
  public String getReagentAnnotationRef(int screenIndex, int reagentIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getReagentAnnotationRef(screenIndex, reagentIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getReagentDescription(int, int) */
  public String getReagentDescription(int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getReagentDescription(screenIndex, reagentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getReagentID(int, int) */
  public String getReagentID(int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getReagentID(screenIndex, reagentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getReagentName(int, int) */
  public String getReagentName(int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getReagentName(screenIndex, reagentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getReagentReagentIdentifier(int, int) */
  public String getReagentReagentIdentifier(int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getReagentReagentIdentifier(screenIndex, reagentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ReagentAnnotationRef property retrieval -

  // - Rectangle property retrieval -

  /* @see MetadataRetrieve#getRectangleDescription(int, int) */
  public String getRectangleDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectangleDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleFill(int, int) */
  public Integer getRectangleFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getRectangleFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleFontSize(int, int) */
  public Integer getRectangleFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getRectangleFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleHeight(int, int) */
  public Double getRectangleHeight(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getRectangleHeight(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleID(int, int) */
  public String getRectangleID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectangleID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleLabel(int, int) */
  public String getRectangleLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectangleLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleName(int, int) */
  public String getRectangleName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectangleName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleStroke(int, int) */
  public Integer getRectangleStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getRectangleStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleStrokeDashArray(int, int) */
  public String getRectangleStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectangleStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleStrokeWidth(int, int) */
  public Double getRectangleStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getRectangleStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleTheC(int, int) */
  public Integer getRectangleTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getRectangleTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleTheT(int, int) */
  public Integer getRectangleTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getRectangleTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleTheZ(int, int) */
  public Integer getRectangleTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getRectangleTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleTransform(int, int) */
  public String getRectangleTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectangleTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleWidth(int, int) */
  public Double getRectangleWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getRectangleWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleX(int, int) */
  public Double getRectangleX(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getRectangleX(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectangleY(int, int) */
  public Double getRectangleY(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getRectangleY(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Screen property retrieval -

  /* @see MetadataRetrieve#getScreenAnnotationRef(int, int) */
  public String getScreenAnnotationRef(int screenIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenAnnotationRef(screenIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenDescription(int) */
  public String getScreenDescription(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenDescription(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenID(int) */
  public String getScreenID(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenID(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenName(int) */
  public String getScreenName(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenName(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenPlateRef(int, int) */
  public String getScreenPlateRef(int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenPlateRef(screenIndex, plateRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenProtocolDescription(int) */
  public String getScreenProtocolDescription(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenProtocolDescription(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenProtocolIdentifier(int) */
  public String getScreenProtocolIdentifier(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenProtocolIdentifier(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenReagentSetDescription(int) */
  public String getScreenReagentSetDescription(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenReagentSetDescription(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenReagentSetIdentifier(int) */
  public String getScreenReagentSetIdentifier(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenReagentSetIdentifier(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenType(int) */
  public String getScreenType(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenType(screenIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ScreenAnnotationRef property retrieval -

  // - ScreenRef property retrieval -

  // - ShapeAnnotationRef property retrieval -

  // - StageLabel property retrieval -

  /* @see MetadataRetrieve#getStageLabelName(int) */
  public String getStageLabelName(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getStageLabelName(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStageLabelX(int) */
  public Double getStageLabelX(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getStageLabelX(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStageLabelY(int) */
  public Double getStageLabelY(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getStageLabelY(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStageLabelZ(int) */
  public Double getStageLabelZ(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getStageLabelZ(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - StringAnnotation property retrieval -

  /* @see MetadataRetrieve#getStringAnnotationID(int) */
  public String getStringAnnotationID(int stringAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getStringAnnotationID(stringAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStringAnnotationNamespace(int) */
  public String getStringAnnotationNamespace(int stringAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getStringAnnotationNamespace(stringAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStringAnnotationValue(int) */
  public String getStringAnnotationValue(int stringAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getStringAnnotationValue(stringAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Text property retrieval -

  /* @see MetadataRetrieve#getTextDescription(int, int) */
  public String getTextDescription(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextDescription(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextFill(int, int) */
  public Integer getTextFill(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTextFill(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextFontSize(int, int) */
  public Integer getTextFontSize(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTextFontSize(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextID(int, int) */
  public String getTextID(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextID(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextLabel(int, int) */
  public String getTextLabel(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextLabel(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextName(int, int) */
  public String getTextName(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextName(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextStroke(int, int) */
  public Integer getTextStroke(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTextStroke(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextStrokeDashArray(int, int) */
  public String getTextStrokeDashArray(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextStrokeDashArray(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextStrokeWidth(int, int) */
  public Double getTextStrokeWidth(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getTextStrokeWidth(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextTheC(int, int) */
  public Integer getTextTheC(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTextTheC(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextTheT(int, int) */
  public Integer getTextTheT(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTextTheT(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextTheZ(int, int) */
  public Integer getTextTheZ(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTextTheZ(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextTransform(int, int) */
  public String getTextTransform(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextTransform(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextValue(int, int) */
  public String getTextValue(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTextValue(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextX(int, int) */
  public Double getTextX(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getTextX(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTextY(int, int) */
  public Double getTextY(int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getTextY(roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - TiffData property retrieval -

  /* @see MetadataRetrieve#getTiffDataFirstC(int, int) */
  public Integer getTiffDataFirstC(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataFirstC(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataFirstT(int, int) */
  public Integer getTiffDataFirstT(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataFirstT(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataFirstZ(int, int) */
  public Integer getTiffDataFirstZ(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataFirstZ(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataIFD(int, int) */
  public Integer getTiffDataIFD(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataIFD(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataPlaneCount(int, int) */
  public Integer getTiffDataPlaneCount(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataPlaneCount(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - TimestampAnnotation property retrieval -

  /* @see MetadataRetrieve#getTimestampAnnotationID(int) */
  public String getTimestampAnnotationID(int timestampAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTimestampAnnotationID(timestampAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTimestampAnnotationNamespace(int) */
  public String getTimestampAnnotationNamespace(int timestampAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTimestampAnnotationNamespace(timestampAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTimestampAnnotationValue(int) */
  public String getTimestampAnnotationValue(int timestampAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTimestampAnnotationValue(timestampAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - TransmittanceRange property retrieval -

  /* @see MetadataRetrieve#getTransmittanceRangeCutIn(int, int) */
  public Integer getTransmittanceRangeCutIn(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTransmittanceRangeCutIn(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTransmittanceRangeCutInTolerance(int, int) */
  public Integer getTransmittanceRangeCutInTolerance(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTransmittanceRangeCutInTolerance(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTransmittanceRangeCutOut(int, int) */
  public Integer getTransmittanceRangeCutOut(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTransmittanceRangeCutOut(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTransmittanceRangeCutOutTolerance(int, int) */
  public Integer getTransmittanceRangeCutOutTolerance(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTransmittanceRangeCutOutTolerance(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTransmittanceRangeTransmittance(int, int) */
  public PercentFraction getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        PercentFraction result = retrieve.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - UUID property retrieval -

  public String getUUIDValue(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getUUIDValue(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getUUIDFileName(int, int) */
  public String getUUIDFileName(int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getUUIDFileName(imageIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Well property retrieval -

  /* @see MetadataRetrieve#getWellAnnotationRef(int, int, int) */
  public String getWellAnnotationRef(int plateIndex, int wellIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellAnnotationRef(plateIndex, wellIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellColor(int, int) */
  public Integer getWellColor(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getWellColor(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellColumn(int, int) */
  public NonNegativeInteger getWellColumn(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        NonNegativeInteger result = retrieve.getWellColumn(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellExternalDescription(int, int) */
  public String getWellExternalDescription(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellExternalDescription(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellExternalIdentifier(int, int) */
  public String getWellExternalIdentifier(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellExternalIdentifier(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellID(int, int) */
  public String getWellID(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellID(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellReagentRef(int, int) */
  public String getWellReagentRef(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellReagentRef(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellRow(int, int) */
  public NonNegativeInteger getWellRow(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        NonNegativeInteger result = retrieve.getWellRow(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellStatus(int, int) */
  public String getWellStatus(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellStatus(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - WellAnnotationRef property retrieval -

  // - WellSample property retrieval -

  /* @see MetadataRetrieve#getWellSampleAnnotationRef(int, int, int, int) */
  public String getWellSampleAnnotationRef(int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellSampleAnnotationRef(plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSampleID(int, int, int) */
  public String getWellSampleID(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellSampleID(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSampleImageRef(int, int, int) */
  public String getWellSampleImageRef(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellSampleImageRef(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSampleIndex(int, int, int) */
  public NonNegativeInteger getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        NonNegativeInteger result = retrieve.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSamplePositionX(int, int, int) */
  public Double getWellSamplePositionX(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getWellSamplePositionX(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSamplePositionY(int, int, int) */
  public Double getWellSamplePositionY(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Double result = retrieve.getWellSamplePositionY(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSampleTimepoint(int, int, int) */
  public Integer getWellSampleTimepoint(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getWellSampleTimepoint(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - WellSampleAnnotationRef property retrieval -

  // - WellSampleRef property retrieval -

  // - XMLAnnotation property retrieval -

  /* @see MetadataRetrieve#getXMLAnnotationID(int) */
  public String getXMLAnnotationID(int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getXMLAnnotationID(xmlAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getXMLAnnotationNamespace(int) */
  public String getXMLAnnotationNamespace(int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getXMLAnnotationNamespace(xmlAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getXMLAnnotationValue(int) */
  public String getXMLAnnotationValue(int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getXMLAnnotationValue(xmlAnnotationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // -- MetadataStore API methods --

  /* @see MetadataStore#createRoot() */
  public void createRoot() {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        ((MetadataStore) o).createRoot();
      }
    }
  }

  /**
   * Unsupported with an AggregateMetadata.
   * @throws RuntimeException Always.
   */
  public Object getRoot() {
    throw new RuntimeException("Unsupported with AggregateMetadata. " +
      "Use getDelegates() and getRoot().");
  }

  /**
   * Unsupported with an AggregateMetadata.
   * @throws RuntimeException Always.
   */
  public void setRoot(Object root) {
    throw new RuntimeException("Unsupported with AggregateMetadata. " +
      "Use getDelegates() and setRoot().");
  }

  /* @see MetadataStore#setUUID(String) */
  public void setUUID(String uuid) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        ((MetadataStore) o).setUUID(uuid);
      }
    }
  }

  // - Arc property storage -

  /* @see MetadataStore#setArcID(String, int, int) */
  public void setArcID(String id, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcID(id, instrumentIndex, arcIndex);
      }
    }
  }

  /* @see MetadataStore#setArcLotNumber(String, int, int) */
  public void setArcLotNumber(String lotNumber, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcLotNumber(lotNumber, instrumentIndex, arcIndex);
      }
    }
  }

  /* @see MetadataStore#setArcManufacturer(String, int, int) */
  public void setArcManufacturer(String manufacturer, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcManufacturer(manufacturer, instrumentIndex, arcIndex);
      }
    }
  }

  /* @see MetadataStore#setArcModel(String, int, int) */
  public void setArcModel(String model, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcModel(model, instrumentIndex, arcIndex);
      }
    }
  }

  /* @see MetadataStore#setArcPower(Double, int, int) */
  public void setArcPower(Double power, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcPower(power, instrumentIndex, arcIndex);
      }
    }
  }

  /* @see MetadataStore#setArcSerialNumber(String, int, int) */
  public void setArcSerialNumber(String serialNumber, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcSerialNumber(serialNumber, instrumentIndex, arcIndex);
      }
    }
  }

  /* @see MetadataStore#setArcType(ArcType, int, int) */
  public void setArcType(ArcType type, int instrumentIndex, int arcIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcType(type, instrumentIndex, arcIndex);
      }
    }
  }

  // - BooleanAnnotation property storage -

  /* @see MetadataStore#setBooleanAnnotationID(String, int) */
  public void setBooleanAnnotationID(String id, int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setBooleanAnnotationID(id, booleanAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setBooleanAnnotationNamespace(String, int) */
  public void setBooleanAnnotationNamespace(String namespace, int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setBooleanAnnotationNamespace(namespace, booleanAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setBooleanAnnotationValue(Boolean, int) */
  public void setBooleanAnnotationValue(Boolean value, int booleanAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setBooleanAnnotationValue(value, booleanAnnotationIndex);
      }
    }
  }

  // - Channel property storage -

  /* @see MetadataStore#setChannelAcquisitionMode(AcquisitionMode, int, int) */
  public void setChannelAcquisitionMode(AcquisitionMode acquisitionMode, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelAcquisitionMode(acquisitionMode, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelAnnotationRef(String, int, int, int) */
  public void setChannelAnnotationRef(String annotationRef, int imageIndex, int channelIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelAnnotationRef(annotationRef, imageIndex, channelIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelColor(Integer, int, int) */
  public void setChannelColor(Integer color, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelColor(color, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelContrastMethod(ContrastMethod, int, int) */
  public void setChannelContrastMethod(ContrastMethod contrastMethod, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelContrastMethod(contrastMethod, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelEmissionWavelength(PositiveInteger, int, int) */
  public void setChannelEmissionWavelength(PositiveInteger emissionWavelength, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelEmissionWavelength(emissionWavelength, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelExcitationWavelength(PositiveInteger, int, int) */
  public void setChannelExcitationWavelength(PositiveInteger excitationWavelength, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelExcitationWavelength(excitationWavelength, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelFilterSetRef(String, int, int) */
  public void setChannelFilterSetRef(String filterSetRef, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelFilterSetRef(filterSetRef, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelFluor(String, int, int) */
  public void setChannelFluor(String fluor, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelFluor(fluor, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelID(String, int, int) */
  public void setChannelID(String id, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelID(id, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelIlluminationType(IlluminationType, int, int) */
  public void setChannelIlluminationType(IlluminationType illuminationType, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelIlluminationType(illuminationType, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelLightSourceSettingsAttenuation(PercentFraction, int, int) */
  public void setChannelLightSourceSettingsAttenuation(PercentFraction lightSourceSettingsAttenuation, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelLightSourceSettingsAttenuation(lightSourceSettingsAttenuation, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelLightSourceSettingsID(String, int, int) */
  public void setChannelLightSourceSettingsID(String lightSourceSettingsID, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelLightSourceSettingsID(lightSourceSettingsID, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelLightSourceSettingsWavelength(PositiveInteger, int, int) */
  public void setChannelLightSourceSettingsWavelength(PositiveInteger lightSourceSettingsWavelength, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelLightSourceSettingsWavelength(lightSourceSettingsWavelength, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelNDFilter(Double, int, int) */
  public void setChannelNDFilter(Double ndFilter, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelNDFilter(ndFilter, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelName(String, int, int) */
  public void setChannelName(String name, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelName(name, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelOTFRef(String, int, int) */
  public void setChannelOTFRef(String otfRef, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelOTFRef(otfRef, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelPinholeSize(Double, int, int) */
  public void setChannelPinholeSize(Double pinholeSize, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelPinholeSize(pinholeSize, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelPockelCellSetting(Integer, int, int) */
  public void setChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelPockelCellSetting(pockelCellSetting, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelSamplesPerPixel(Integer, int, int) */
  public void setChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelSamplesPerPixel(samplesPerPixel, imageIndex, channelIndex);
      }
    }
  }

  // - ChannelAnnotationRef property storage -

  // - Dataset property storage -

  /* @see MetadataStore#setDatasetAnnotationRef(String, int, int) */
  public void setDatasetAnnotationRef(String annotationRef, int datasetIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetAnnotationRef(annotationRef, datasetIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setDatasetDescription(String, int) */
  public void setDatasetDescription(String description, int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetDescription(description, datasetIndex);
      }
    }
  }

  /* @see MetadataStore#setDatasetExperimenterRef(String, int) */
  public void setDatasetExperimenterRef(String experimenterRef, int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetExperimenterRef(experimenterRef, datasetIndex);
      }
    }
  }

  /* @see MetadataStore#setDatasetGroupRef(String, int) */
  public void setDatasetGroupRef(String groupRef, int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetGroupRef(groupRef, datasetIndex);
      }
    }
  }

  /* @see MetadataStore#setDatasetID(String, int) */
  public void setDatasetID(String id, int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetID(id, datasetIndex);
      }
    }
  }

  /* @see MetadataStore#setDatasetName(String, int) */
  public void setDatasetName(String name, int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetName(name, datasetIndex);
      }
    }
  }

  /* @see MetadataStore#setDatasetProjectRef(String, int, int) */
  public void setDatasetProjectRef(String projectRef, int datasetIndex, int projectRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetProjectRef(projectRef, datasetIndex, projectRefIndex);
      }
    }
  }

  // - DatasetAnnotationRef property storage -

  // - DatasetRef property storage -

  // - Detector property storage -

  /* @see MetadataStore#setDetectorAmplificationGain(Double, int, int) */
  public void setDetectorAmplificationGain(Double amplificationGain, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorGain(Double, int, int) */
  public void setDetectorGain(Double gain, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorGain(gain, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorID(String, int, int) */
  public void setDetectorID(String id, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorID(id, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorLotNumber(String, int, int) */
  public void setDetectorLotNumber(String lotNumber, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorLotNumber(lotNumber, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorManufacturer(String, int, int) */
  public void setDetectorManufacturer(String manufacturer, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorManufacturer(manufacturer, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorModel(String, int, int) */
  public void setDetectorModel(String model, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorModel(model, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorOffset(Double, int, int) */
  public void setDetectorOffset(Double offset, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorOffset(offset, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSerialNumber(String, int, int) */
  public void setDetectorSerialNumber(String serialNumber, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSerialNumber(serialNumber, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorType(DetectorType, int, int) */
  public void setDetectorType(DetectorType type, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorType(type, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorVoltage(Double, int, int) */
  public void setDetectorVoltage(Double voltage, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorZoom(Double, int, int) */
  public void setDetectorZoom(Double zoom, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
      }
    }
  }

  // - DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsBinning(Binning, int, int) */
  public void setDetectorSettingsBinning(Binning binning, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsBinning(binning, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsGain(Double, int, int) */
  public void setDetectorSettingsGain(Double gain, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsGain(gain, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsID(String, int, int) */
  public void setDetectorSettingsID(String id, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsID(id, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Double, int, int) */
  public void setDetectorSettingsOffset(Double offset, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsOffset(offset, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsReadOutRate(Double, int, int) */
  public void setDetectorSettingsReadOutRate(Double readOutRate, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsReadOutRate(readOutRate, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsVoltage(Double, int, int) */
  public void setDetectorSettingsVoltage(Double voltage, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsVoltage(voltage, imageIndex, channelIndex);
      }
    }
  }

  // - Dichroic property storage -

  /* @see MetadataStore#setDichroicID(String, int, int) */
  public void setDichroicID(String id, int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDichroicID(id, instrumentIndex, dichroicIndex);
      }
    }
  }

  /* @see MetadataStore#setDichroicLotNumber(String, int, int) */
  public void setDichroicLotNumber(String lotNumber, int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDichroicLotNumber(lotNumber, instrumentIndex, dichroicIndex);
      }
    }
  }

  /* @see MetadataStore#setDichroicManufacturer(String, int, int) */
  public void setDichroicManufacturer(String manufacturer, int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDichroicManufacturer(manufacturer, instrumentIndex, dichroicIndex);
      }
    }
  }

  /* @see MetadataStore#setDichroicModel(String, int, int) */
  public void setDichroicModel(String model, int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDichroicModel(model, instrumentIndex, dichroicIndex);
      }
    }
  }

  /* @see MetadataStore#setDichroicSerialNumber(String, int, int) */
  public void setDichroicSerialNumber(String serialNumber, int instrumentIndex, int dichroicIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDichroicSerialNumber(serialNumber, instrumentIndex, dichroicIndex);
      }
    }
  }

  // - DoubleAnnotation property storage -

  /* @see MetadataStore#setDoubleAnnotationID(String, int) */
  public void setDoubleAnnotationID(String id, int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDoubleAnnotationID(id, doubleAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setDoubleAnnotationNamespace(String, int) */
  public void setDoubleAnnotationNamespace(String namespace, int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDoubleAnnotationNamespace(namespace, doubleAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setDoubleAnnotationValue(Double, int) */
  public void setDoubleAnnotationValue(Double value, int doubleAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDoubleAnnotationValue(value, doubleAnnotationIndex);
      }
    }
  }

  // - Ellipse property storage -

  /* @see MetadataStore#setEllipseDescription(String, int, int) */
  public void setEllipseDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseFill(Integer, int, int) */
  public void setEllipseFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseFontSize(Integer, int, int) */
  public void setEllipseFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseID(String, int, int) */
  public void setEllipseID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseLabel(String, int, int) */
  public void setEllipseLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseName(String, int, int) */
  public void setEllipseName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseRadiusX(Double, int, int) */
  public void setEllipseRadiusX(Double radiusX, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseRadiusX(radiusX, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseRadiusY(Double, int, int) */
  public void setEllipseRadiusY(Double radiusY, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseRadiusY(radiusY, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseStroke(Integer, int, int) */
  public void setEllipseStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseStrokeDashArray(String, int, int) */
  public void setEllipseStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseStrokeWidth(Double, int, int) */
  public void setEllipseStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseTheC(Integer, int, int) */
  public void setEllipseTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseTheT(Integer, int, int) */
  public void setEllipseTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseTheZ(Integer, int, int) */
  public void setEllipseTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseTransform(String, int, int) */
  public void setEllipseTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseX(Double, int, int) */
  public void setEllipseX(Double x, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseX(x, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseY(Double, int, int) */
  public void setEllipseY(Double y, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseY(y, roiIndex, shapeIndex);
      }
    }
  }

  // - Experiment property storage -

  /* @see MetadataStore#setExperimentDescription(String, int) */
  public void setExperimentDescription(String description, int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimentDescription(description, experimentIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimentExperimenterRef(String, int) */
  public void setExperimentExperimenterRef(String experimenterRef, int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimentExperimenterRef(experimenterRef, experimentIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimentID(String, int) */
  public void setExperimentID(String id, int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimentID(id, experimentIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimentType(ExperimentType, int) */
  public void setExperimentType(ExperimentType type, int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimentType(type, experimentIndex);
      }
    }
  }

  // - Experimenter property storage -

  /* @see MetadataStore#setExperimenterAnnotationRef(String, int, int) */
  public void setExperimenterAnnotationRef(String annotationRef, int experimenterIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterAnnotationRef(annotationRef, experimenterIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterDisplayName(String, int) */
  public void setExperimenterDisplayName(String displayName, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterDisplayName(displayName, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterEmail(String, int) */
  public void setExperimenterEmail(String email, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterEmail(email, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterFirstName(String, int) */
  public void setExperimenterFirstName(String firstName, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterFirstName(firstName, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterGroupRef(String, int, int) */
  public void setExperimenterGroupRef(String groupRef, int experimenterIndex, int groupRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterGroupRef(groupRef, experimenterIndex, groupRefIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterID(String, int) */
  public void setExperimenterID(String id, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterID(id, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterInstitution(String, int) */
  public void setExperimenterInstitution(String institution, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterInstitution(institution, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterLastName(String, int) */
  public void setExperimenterLastName(String lastName, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterLastName(lastName, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterMiddleName(String, int) */
  public void setExperimenterMiddleName(String middleName, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterMiddleName(middleName, experimenterIndex);
      }
    }
  }

  /* @see MetadataStore#setExperimenterUserName(String, int) */
  public void setExperimenterUserName(String userName, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterUserName(userName, experimenterIndex);
      }
    }
  }

  // - ExperimenterAnnotationRef property storage -

  // - ExperimenterGroupRef property storage -

  // - Filament property storage -

  /* @see MetadataStore#setFilamentID(String, int, int) */
  public void setFilamentID(String id, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentID(id, instrumentIndex, filamentIndex);
      }
    }
  }

  /* @see MetadataStore#setFilamentLotNumber(String, int, int) */
  public void setFilamentLotNumber(String lotNumber, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentLotNumber(lotNumber, instrumentIndex, filamentIndex);
      }
    }
  }

  /* @see MetadataStore#setFilamentManufacturer(String, int, int) */
  public void setFilamentManufacturer(String manufacturer, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentManufacturer(manufacturer, instrumentIndex, filamentIndex);
      }
    }
  }

  /* @see MetadataStore#setFilamentModel(String, int, int) */
  public void setFilamentModel(String model, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentModel(model, instrumentIndex, filamentIndex);
      }
    }
  }

  /* @see MetadataStore#setFilamentPower(Double, int, int) */
  public void setFilamentPower(Double power, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentPower(power, instrumentIndex, filamentIndex);
      }
    }
  }

  /* @see MetadataStore#setFilamentSerialNumber(String, int, int) */
  public void setFilamentSerialNumber(String serialNumber, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentSerialNumber(serialNumber, instrumentIndex, filamentIndex);
      }
    }
  }

  /* @see MetadataStore#setFilamentType(FilamentType, int, int) */
  public void setFilamentType(FilamentType type, int instrumentIndex, int filamentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentType(type, instrumentIndex, filamentIndex);
      }
    }
  }

  // - FileAnnotation property storage -

  /* @see MetadataStore#setFileAnnotationBinaryFileFileName(String, int) */
  public void setFileAnnotationBinaryFileFileName(String binaryFileFileName, int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFileAnnotationBinaryFileFileName(binaryFileFileName, fileAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setFileAnnotationBinaryFileMIMEType(String, int) */
  public void setFileAnnotationBinaryFileMIMEType(String binaryFileMIMEType, int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFileAnnotationBinaryFileMIMEType(binaryFileMIMEType, fileAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setFileAnnotationBinaryFileSize(Integer, int) */
  public void setFileAnnotationBinaryFileSize(Integer binaryFileSize, int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFileAnnotationBinaryFileSize(binaryFileSize, fileAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setFileAnnotationID(String, int) */
  public void setFileAnnotationID(String id, int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFileAnnotationID(id, fileAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setFileAnnotationNamespace(String, int) */
  public void setFileAnnotationNamespace(String namespace, int fileAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFileAnnotationNamespace(namespace, fileAnnotationIndex);
      }
    }
  }

  // - Filter property storage -

  /* @see MetadataStore#setFilterFilterWheel(String, int, int) */
  public void setFilterFilterWheel(String filterWheel, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterFilterWheel(filterWheel, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterID(String, int, int) */
  public void setFilterID(String id, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterID(id, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterLotNumber(String, int, int) */
  public void setFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterManufacturer(String, int, int) */
  public void setFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterModel(String, int, int) */
  public void setFilterModel(String model, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterModel(model, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSerialNumber(String, int, int) */
  public void setFilterSerialNumber(String serialNumber, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSerialNumber(serialNumber, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterType(FilterType, int, int) */
  public void setFilterType(FilterType type, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterType(type, instrumentIndex, filterIndex);
      }
    }
  }

  // - FilterSet property storage -

  /* @see MetadataStore#setFilterSetDichroicRef(String, int, int) */
  public void setFilterSetDichroicRef(String dichroicRef, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetDichroicRef(dichroicRef, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetEmissionFilterRef(String, int, int, int) */
  public void setFilterSetEmissionFilterRef(String emissionFilterRef, int instrumentIndex, int filterSetIndex, int emissionFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetEmissionFilterRef(emissionFilterRef, instrumentIndex, filterSetIndex, emissionFilterRefIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetExcitationFilterRef(String, int, int, int) */
  public void setFilterSetExcitationFilterRef(String excitationFilterRef, int instrumentIndex, int filterSetIndex, int excitationFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetExcitationFilterRef(excitationFilterRef, instrumentIndex, filterSetIndex, excitationFilterRefIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetID(String, int, int) */
  public void setFilterSetID(String id, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetID(id, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetLotNumber(String, int, int) */
  public void setFilterSetLotNumber(String lotNumber, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetLotNumber(lotNumber, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetManufacturer(String, int, int) */
  public void setFilterSetManufacturer(String manufacturer, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetManufacturer(manufacturer, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetModel(String, int, int) */
  public void setFilterSetModel(String model, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetModel(model, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetSerialNumber(String, int, int) */
  public void setFilterSetSerialNumber(String serialNumber, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetSerialNumber(serialNumber, instrumentIndex, filterSetIndex);
      }
    }
  }

  // - FilterSetEmissionFilterRef property storage -

  // - FilterSetExcitationFilterRef property storage -

  // - Group property storage -

  /* @see MetadataStore#setGroupContact(String, int) */
  public void setGroupContact(String contact, int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setGroupContact(contact, groupIndex);
      }
    }
  }

  /* @see MetadataStore#setGroupDescription(String, int) */
  public void setGroupDescription(String description, int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setGroupDescription(description, groupIndex);
      }
    }
  }

  /* @see MetadataStore#setGroupID(String, int) */
  public void setGroupID(String id, int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setGroupID(id, groupIndex);
      }
    }
  }

  /* @see MetadataStore#setGroupLeader(String, int) */
  public void setGroupLeader(String leader, int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setGroupLeader(leader, groupIndex);
      }
    }
  }

  /* @see MetadataStore#setGroupName(String, int) */
  public void setGroupName(String name, int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setGroupName(name, groupIndex);
      }
    }
  }

  // - Image property storage -

  /* @see MetadataStore#setImageAcquiredDate(String, int) */
  public void setImageAcquiredDate(String acquiredDate, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageAcquiredDate(acquiredDate, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageAnnotationRef(String, int, int) */
  public void setImageAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageAnnotationRef(annotationRef, imageIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setImageDatasetRef(String, int, int) */
  public void setImageDatasetRef(String datasetRef, int imageIndex, int datasetRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageDatasetRef(datasetRef, imageIndex, datasetRefIndex);
      }
    }
  }

  /* @see MetadataStore#setImageDescription(String, int) */
  public void setImageDescription(String description, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageDescription(description, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageExperimentRef(String, int) */
  public void setImageExperimentRef(String experimentRef, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageExperimentRef(experimentRef, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageExperimenterRef(String, int) */
  public void setImageExperimenterRef(String experimenterRef, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageExperimenterRef(experimenterRef, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageGroupRef(String, int) */
  public void setImageGroupRef(String groupRef, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageGroupRef(groupRef, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageID(String, int) */
  public void setImageID(String id, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageID(id, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageInstrumentRef(String, int) */
  public void setImageInstrumentRef(String instrumentRef, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageInstrumentRef(instrumentRef, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageMicrobeamManipulationRef(String, int, int) */
  public void setImageMicrobeamManipulationRef(String microbeamManipulationRef, int imageIndex, int microbeamManipulationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageMicrobeamManipulationRef(microbeamManipulationRef, imageIndex, microbeamManipulationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setImageName(String, int) */
  public void setImageName(String name, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageName(name, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageObjectiveSettingsCorrectionCollar(Double, int) */
  public void setImageObjectiveSettingsCorrectionCollar(Double objectiveSettingsCorrectionCollar, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollar, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageObjectiveSettingsID(String, int) */
  public void setImageObjectiveSettingsID(String objectiveSettingsID, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageObjectiveSettingsID(objectiveSettingsID, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageObjectiveSettingsMedium(Medium, int) */
  public void setImageObjectiveSettingsMedium(Medium objectiveSettingsMedium, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageObjectiveSettingsMedium(objectiveSettingsMedium, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageObjectiveSettingsRefractiveIndex(Double, int) */
  public void setImageObjectiveSettingsRefractiveIndex(Double objectiveSettingsRefractiveIndex, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndex, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageROIRef(String, int, int) */
  public void setImageROIRef(String roiRef, int imageIndex, int roiRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageROIRef(roiRef, imageIndex, roiRefIndex);
      }
    }
  }

  // - ImageAnnotationRef property storage -

  // - ImageROIRef property storage -

  // - ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Double, int) */
  public void setImagingEnvironmentAirPressure(Double airPressure, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(PercentFraction, int) */
  public void setImagingEnvironmentCO2Percent(PercentFraction cO2Percent, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(PercentFraction, int) */
  public void setImagingEnvironmentHumidity(PercentFraction humidity, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentHumidity(humidity, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Double, int) */
  public void setImagingEnvironmentTemperature(Double temperature, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentTemperature(temperature, imageIndex);
      }
    }
  }

  // - Instrument property storage -

  /* @see MetadataStore#setInstrumentID(String, int) */
  public void setInstrumentID(String id, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setInstrumentID(id, instrumentIndex);
      }
    }
  }

  // - Laser property storage -

  /* @see MetadataStore#setLaserFrequencyMultiplication(PositiveInteger, int, int) */
  public void setLaserFrequencyMultiplication(PositiveInteger frequencyMultiplication, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserID(String, int, int) */
  public void setLaserID(String id, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserID(id, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserLaserMedium(LaserMedium, int, int) */
  public void setLaserLaserMedium(LaserMedium laserMedium, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserLaserMedium(laserMedium, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserLotNumber(String, int, int) */
  public void setLaserLotNumber(String lotNumber, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserLotNumber(lotNumber, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserManufacturer(String, int, int) */
  public void setLaserManufacturer(String manufacturer, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserManufacturer(manufacturer, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserModel(String, int, int) */
  public void setLaserModel(String model, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserModel(model, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserPockelCell(Boolean, int, int) */
  public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPockelCell(pockelCell, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserPower(Double, int, int) */
  public void setLaserPower(Double power, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPower(power, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserPulse(Pulse, int, int) */
  public void setLaserPulse(Pulse pulse, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPulse(pulse, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserPump(String, int, int) */
  public void setLaserPump(String pump, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPump(pump, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserRepetitionRate(Double, int, int) */
  public void setLaserRepetitionRate(Double repetitionRate, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserRepetitionRate(repetitionRate, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserSerialNumber(String, int, int) */
  public void setLaserSerialNumber(String serialNumber, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserSerialNumber(serialNumber, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserTuneable(tuneable, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserType(LaserType, int, int) */
  public void setLaserType(LaserType type, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserType(type, instrumentIndex, laserIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserWavelength(PositiveInteger, int, int) */
  public void setLaserWavelength(PositiveInteger wavelength, int instrumentIndex, int laserIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserWavelength(wavelength, instrumentIndex, laserIndex);
      }
    }
  }

  // - LightEmittingDiode property storage -

  /* @see MetadataStore#setLightEmittingDiodeID(String, int, int) */
  public void setLightEmittingDiodeID(String id, int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightEmittingDiodeID(id, instrumentIndex, lightEmittingDiodeIndex);
      }
    }
  }

  /* @see MetadataStore#setLightEmittingDiodeLotNumber(String, int, int) */
  public void setLightEmittingDiodeLotNumber(String lotNumber, int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightEmittingDiodeLotNumber(lotNumber, instrumentIndex, lightEmittingDiodeIndex);
      }
    }
  }

  /* @see MetadataStore#setLightEmittingDiodeManufacturer(String, int, int) */
  public void setLightEmittingDiodeManufacturer(String manufacturer, int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightEmittingDiodeManufacturer(manufacturer, instrumentIndex, lightEmittingDiodeIndex);
      }
    }
  }

  /* @see MetadataStore#setLightEmittingDiodeModel(String, int, int) */
  public void setLightEmittingDiodeModel(String model, int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightEmittingDiodeModel(model, instrumentIndex, lightEmittingDiodeIndex);
      }
    }
  }

  /* @see MetadataStore#setLightEmittingDiodePower(Double, int, int) */
  public void setLightEmittingDiodePower(Double power, int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightEmittingDiodePower(power, instrumentIndex, lightEmittingDiodeIndex);
      }
    }
  }

  /* @see MetadataStore#setLightEmittingDiodeSerialNumber(String, int, int) */
  public void setLightEmittingDiodeSerialNumber(String serialNumber, int instrumentIndex, int lightEmittingDiodeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightEmittingDiodeSerialNumber(serialNumber, instrumentIndex, lightEmittingDiodeIndex);
      }
    }
  }

  // - LightPath property storage -

  /* @see MetadataStore#setLightPathDichroicRef(String, int, int) */
  public void setLightPathDichroicRef(String dichroicRef, int imageIndex, int channelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightPathDichroicRef(dichroicRef, imageIndex, channelIndex);
      }
    }
  }

  /* @see MetadataStore#setLightPathEmissionFilterRef(String, int, int, int) */
  public void setLightPathEmissionFilterRef(String emissionFilterRef, int imageIndex, int channelIndex, int emissionFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightPathEmissionFilterRef(emissionFilterRef, imageIndex, channelIndex, emissionFilterRefIndex);
      }
    }
  }

  /* @see MetadataStore#setLightPathExcitationFilterRef(String, int, int, int) */
  public void setLightPathExcitationFilterRef(String excitationFilterRef, int imageIndex, int channelIndex, int excitationFilterRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightPathExcitationFilterRef(excitationFilterRef, imageIndex, channelIndex, excitationFilterRefIndex);
      }
    }
  }

  // - LightPathEmissionFilterRef property storage -

  // - LightPathExcitationFilterRef property storage -

  // - Line property storage -

  /* @see MetadataStore#setLineDescription(String, int, int) */
  public void setLineDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineFill(Integer, int, int) */
  public void setLineFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineFontSize(Integer, int, int) */
  public void setLineFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineID(String, int, int) */
  public void setLineID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineLabel(String, int, int) */
  public void setLineLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineName(String, int, int) */
  public void setLineName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineStroke(Integer, int, int) */
  public void setLineStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineStrokeDashArray(String, int, int) */
  public void setLineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineStrokeWidth(Double, int, int) */
  public void setLineStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineTheC(Integer, int, int) */
  public void setLineTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineTheT(Integer, int, int) */
  public void setLineTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineTheZ(Integer, int, int) */
  public void setLineTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineTransform(String, int, int) */
  public void setLineTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineX1(Double, int, int) */
  public void setLineX1(Double x1, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineX1(x1, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineX2(Double, int, int) */
  public void setLineX2(Double x2, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineX2(x2, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineY1(Double, int, int) */
  public void setLineY1(Double y1, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineY1(y1, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineY2(Double, int, int) */
  public void setLineY2(Double y2, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineY2(y2, roiIndex, shapeIndex);
      }
    }
  }

  // - ListAnnotation property storage -

  /* @see MetadataStore#setListAnnotationAnnotationRef(String, int, int) */
  public void setListAnnotationAnnotationRef(String annotationRef, int listAnnotationIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setListAnnotationAnnotationRef(annotationRef, listAnnotationIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setListAnnotationID(String, int) */
  public void setListAnnotationID(String id, int listAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setListAnnotationID(id, listAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setListAnnotationNamespace(String, int) */
  public void setListAnnotationNamespace(String namespace, int listAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setListAnnotationNamespace(namespace, listAnnotationIndex);
      }
    }
  }

  // - ListAnnotationAnnotationRef property storage -

  // - LongAnnotation property storage -

  /* @see MetadataStore#setLongAnnotationID(String, int) */
  public void setLongAnnotationID(String id, int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLongAnnotationID(id, longAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setLongAnnotationNamespace(String, int) */
  public void setLongAnnotationNamespace(String namespace, int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLongAnnotationNamespace(namespace, longAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setLongAnnotationValue(Long, int) */
  public void setLongAnnotationValue(Long value, int longAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLongAnnotationValue(value, longAnnotationIndex);
      }
    }
  }

  // - Mask property storage -

  /* @see MetadataStore#setMaskDescription(String, int, int) */
  public void setMaskDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskFill(Integer, int, int) */
  public void setMaskFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskFontSize(Integer, int, int) */
  public void setMaskFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskID(String, int, int) */
  public void setMaskID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskLabel(String, int, int) */
  public void setMaskLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskName(String, int, int) */
  public void setMaskName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskStroke(Integer, int, int) */
  public void setMaskStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskStrokeDashArray(String, int, int) */
  public void setMaskStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskStrokeWidth(Double, int, int) */
  public void setMaskStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskTheC(Integer, int, int) */
  public void setMaskTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskTheT(Integer, int, int) */
  public void setMaskTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskTheZ(Integer, int, int) */
  public void setMaskTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskTransform(String, int, int) */
  public void setMaskTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskX(Double, int, int) */
  public void setMaskX(Double x, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskX(x, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskY(Double, int, int) */
  public void setMaskY(Double y, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskY(y, roiIndex, shapeIndex);
      }
    }
  }

  // - MicrobeamManipulation property storage -

  /* @see MetadataStore#setMicrobeamManipulationExperimenterRef(String, int, int) */
  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationExperimenterRef(experimenterRef, experimentIndex, microbeamManipulationIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationID(String, int, int) */
  public void setMicrobeamManipulationID(String id, int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationID(id, experimentIndex, microbeamManipulationIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationROIRef(String, int, int, int) */
  public void setMicrobeamManipulationROIRef(String roiRef, int experimentIndex, int microbeamManipulationIndex, int roiRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationROIRef(roiRef, experimentIndex, microbeamManipulationIndex, roiRefIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationType(MicrobeamManipulationType, int, int) */
  public void setMicrobeamManipulationType(MicrobeamManipulationType type, int experimentIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationType(type, experimentIndex, microbeamManipulationIndex);
      }
    }
  }

  // - MicrobeamManipulationLightSourceSettings property storage -

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsAttenuation(PercentFraction attenuation, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationLightSourceSettingsAttenuation(attenuation, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsID(String, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsID(String id, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationLightSourceSettingsID(id, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger, int, int, int) */
  public void setMicrobeamManipulationLightSourceSettingsWavelength(PositiveInteger wavelength, int experimentIndex, int microbeamManipulationIndex, int lightSourceSettingsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationLightSourceSettingsWavelength(wavelength, experimentIndex, microbeamManipulationIndex, lightSourceSettingsIndex);
      }
    }
  }

  // - MicrobeamManipulationROIRef property storage -

  // - MicrobeamManipulationRef property storage -

  // - Microscope property storage -

  /* @see MetadataStore#setMicroscopeLotNumber(String, int) */
  public void setMicroscopeLotNumber(String lotNumber, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeLotNumber(lotNumber, instrumentIndex);
      }
    }
  }

  /* @see MetadataStore#setMicroscopeManufacturer(String, int) */
  public void setMicroscopeManufacturer(String manufacturer, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeManufacturer(manufacturer, instrumentIndex);
      }
    }
  }

  /* @see MetadataStore#setMicroscopeModel(String, int) */
  public void setMicroscopeModel(String model, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeModel(model, instrumentIndex);
      }
    }
  }

  /* @see MetadataStore#setMicroscopeSerialNumber(String, int) */
  public void setMicroscopeSerialNumber(String serialNumber, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeSerialNumber(serialNumber, instrumentIndex);
      }
    }
  }

  /* @see MetadataStore#setMicroscopeType(MicroscopeType, int) */
  public void setMicroscopeType(MicroscopeType type, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeType(type, instrumentIndex);
      }
    }
  }

  // - OTF property storage -

  /* @see MetadataStore#setOTFBinaryFileFileName(String, int, int) */
  public void setOTFBinaryFileFileName(String binaryFileFileName, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFBinaryFileFileName(binaryFileFileName, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFBinaryFileMIMEType(String, int, int) */
  public void setOTFBinaryFileMIMEType(String binaryFileMIMEType, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFBinaryFileMIMEType(binaryFileMIMEType, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFBinaryFileSize(Integer, int, int) */
  public void setOTFBinaryFileSize(Integer binaryFileSize, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFBinaryFileSize(binaryFileSize, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFFilterSetRef(String, int, int) */
  public void setOTFFilterSetRef(String filterSetRef, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFFilterSetRef(filterSetRef, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFID(String, int, int) */
  public void setOTFID(String id, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFID(id, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFObjectiveSettingsCorrectionCollar(Double, int, int) */
  public void setOTFObjectiveSettingsCorrectionCollar(Double objectiveSettingsCorrectionCollar, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFObjectiveSettingsCorrectionCollar(objectiveSettingsCorrectionCollar, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFObjectiveSettingsID(String, int, int) */
  public void setOTFObjectiveSettingsID(String objectiveSettingsID, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFObjectiveSettingsID(objectiveSettingsID, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFObjectiveSettingsMedium(Medium, int, int) */
  public void setOTFObjectiveSettingsMedium(Medium objectiveSettingsMedium, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFObjectiveSettingsMedium(objectiveSettingsMedium, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFObjectiveSettingsRefractiveIndex(Double, int, int) */
  public void setOTFObjectiveSettingsRefractiveIndex(Double objectiveSettingsRefractiveIndex, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFObjectiveSettingsRefractiveIndex(objectiveSettingsRefractiveIndex, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFOpticalAxisAveraged(Boolean, int, int) */
  public void setOTFOpticalAxisAveraged(Boolean opticalAxisAveraged, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFOpticalAxisAveraged(opticalAxisAveraged, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFSizeX(PositiveInteger, int, int) */
  public void setOTFSizeX(PositiveInteger sizeX, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFSizeY(PositiveInteger, int, int) */
  public void setOTFSizeY(PositiveInteger sizeY, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFType(PixelType, int, int) */
  public void setOTFType(PixelType type, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFType(type, instrumentIndex, otfIndex);
      }
    }
  }

  // - Objective property storage -

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Double, int, int) */
  public void setObjectiveCalibratedMagnification(Double calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveCorrection(Correction, int, int) */
  public void setObjectiveCorrection(Correction correction, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveCorrection(correction, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveID(String, int, int) */
  public void setObjectiveID(String id, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveID(id, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveImmersion(Immersion, int, int) */
  public void setObjectiveImmersion(Immersion immersion, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveImmersion(immersion, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveIris(Boolean, int, int) */
  public void setObjectiveIris(Boolean iris, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveIris(iris, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveLensNA(Double, int, int) */
  public void setObjectiveLensNA(Double lensNA, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveLotNumber(String, int, int) */
  public void setObjectiveLotNumber(String lotNumber, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveLotNumber(lotNumber, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveManufacturer(String, int, int) */
  public void setObjectiveManufacturer(String manufacturer, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveManufacturer(manufacturer, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveModel(String, int, int) */
  public void setObjectiveModel(String model, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveModel(model, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveNominalMagnification(Integer, int, int) */
  public void setObjectiveNominalMagnification(Integer nominalMagnification, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveNominalMagnification(nominalMagnification, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveSerialNumber(String, int, int) */
  public void setObjectiveSerialNumber(String serialNumber, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveSerialNumber(serialNumber, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveWorkingDistance(Double, int, int) */
  public void setObjectiveWorkingDistance(Double workingDistance, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
      }
    }
  }

  // - Path property storage -

  /* @see MetadataStore#setPathDefinition(String, int, int) */
  public void setPathDefinition(String definition, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathDefinition(definition, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathDescription(String, int, int) */
  public void setPathDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathFill(Integer, int, int) */
  public void setPathFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathFontSize(Integer, int, int) */
  public void setPathFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathID(String, int, int) */
  public void setPathID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathLabel(String, int, int) */
  public void setPathLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathName(String, int, int) */
  public void setPathName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathStroke(Integer, int, int) */
  public void setPathStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathStrokeDashArray(String, int, int) */
  public void setPathStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathStrokeWidth(Double, int, int) */
  public void setPathStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathTheC(Integer, int, int) */
  public void setPathTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathTheT(Integer, int, int) */
  public void setPathTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathTheZ(Integer, int, int) */
  public void setPathTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathTransform(String, int, int) */
  public void setPathTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  // - Pixels property storage -

  /* @see MetadataStore#setPixelsAnnotationRef(String, int, int) */
  public void setPixelsAnnotationRef(String annotationRef, int imageIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsAnnotationRef(annotationRef, imageIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsDimensionOrder(DimensionOrder, int) */
  public void setPixelsDimensionOrder(DimensionOrder dimensionOrder, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsDimensionOrder(dimensionOrder, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsID(String, int) */
  public void setPixelsID(String id, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsID(id, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsPhysicalSizeX(Double, int) */
  public void setPixelsPhysicalSizeX(Double physicalSizeX, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsPhysicalSizeX(physicalSizeX, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsPhysicalSizeY(Double, int) */
  public void setPixelsPhysicalSizeY(Double physicalSizeY, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsPhysicalSizeY(physicalSizeY, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsPhysicalSizeZ(Double, int) */
  public void setPixelsPhysicalSizeZ(Double physicalSizeZ, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsPhysicalSizeZ(physicalSizeZ, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeC(PositiveInteger, int) */
  public void setPixelsSizeC(PositiveInteger sizeC, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeC(sizeC, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeT(PositiveInteger, int) */
  public void setPixelsSizeT(PositiveInteger sizeT, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeT(sizeT, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeX(PositiveInteger, int) */
  public void setPixelsSizeX(PositiveInteger sizeX, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeX(sizeX, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeY(PositiveInteger, int) */
  public void setPixelsSizeY(PositiveInteger sizeY, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeY(sizeY, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeZ(PositiveInteger, int) */
  public void setPixelsSizeZ(PositiveInteger sizeZ, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeZ(sizeZ, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsTimeIncrement(Double, int) */
  public void setPixelsTimeIncrement(Double timeIncrement, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsTimeIncrement(timeIncrement, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsType(PixelType, int) */
  public void setPixelsType(PixelType type, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsType(type, imageIndex);
      }
    }
  }

  // - PixelsAnnotationRef property storage -

  // - PixelsBinData property storage -

  /* @see MetadataStore#setPixelsBinDataBigEndian(Boolean, int, int) */
  public void setPixelsBinDataBigEndian(Boolean bigEndian, int imageIndex, int binDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsBinDataBigEndian(bigEndian, imageIndex, binDataIndex);
      }
    }
  }

  // - Plane property storage -

  /* @see MetadataStore#setPlaneAnnotationRef(String, int, int, int) */
  public void setPlaneAnnotationRef(String annotationRef, int imageIndex, int planeIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneAnnotationRef(annotationRef, imageIndex, planeIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneDeltaT(Double, int, int) */
  public void setPlaneDeltaT(Double deltaT, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneDeltaT(deltaT, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneExposureTime(Double, int, int) */
  public void setPlaneExposureTime(Double exposureTime, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneExposureTime(exposureTime, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneHashSHA1(String, int, int) */
  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneHashSHA1(hashSHA1, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlanePositionX(Double, int, int) */
  public void setPlanePositionX(Double positionX, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlanePositionX(positionX, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlanePositionY(Double, int, int) */
  public void setPlanePositionY(Double positionY, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlanePositionY(positionY, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlanePositionZ(Double, int, int) */
  public void setPlanePositionZ(Double positionZ, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlanePositionZ(positionZ, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTheC(theC, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTheT(theT, imageIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTheZ(theZ, imageIndex, planeIndex);
      }
    }
  }

  // - PlaneAnnotationRef property storage -

  // - Plate property storage -

  /* @see MetadataStore#setPlateAnnotationRef(String, int, int) */
  public void setPlateAnnotationRef(String annotationRef, int plateIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAnnotationRef(annotationRef, plateIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateColumnNamingConvention(NamingConvention, int) */
  public void setPlateColumnNamingConvention(NamingConvention columnNamingConvention, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateColumnNamingConvention(columnNamingConvention, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateColumns(Integer, int) */
  public void setPlateColumns(Integer columns, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateColumns(columns, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateDescription(String, int) */
  public void setPlateDescription(String description, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateDescription(description, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateExternalIdentifier(String, int) */
  public void setPlateExternalIdentifier(String externalIdentifier, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateExternalIdentifier(externalIdentifier, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateID(String, int) */
  public void setPlateID(String id, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateID(id, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateName(String, int) */
  public void setPlateName(String name, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateName(name, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateRowNamingConvention(NamingConvention, int) */
  public void setPlateRowNamingConvention(NamingConvention rowNamingConvention, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateRowNamingConvention(rowNamingConvention, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateRows(Integer, int) */
  public void setPlateRows(Integer rows, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateRows(rows, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateScreenRef(String, int, int) */
  public void setPlateScreenRef(String screenRef, int plateIndex, int screenRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateScreenRef(screenRef, plateIndex, screenRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateStatus(String, int) */
  public void setPlateStatus(String status, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateStatus(status, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateWellOriginX(Double, int) */
  public void setPlateWellOriginX(Double wellOriginX, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateWellOriginX(wellOriginX, plateIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateWellOriginY(Double, int) */
  public void setPlateWellOriginY(Double wellOriginY, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateWellOriginY(wellOriginY, plateIndex);
      }
    }
  }

  // - PlateAcquisition property storage -

  /* @see MetadataStore#setPlateAcquisitionAnnotationRef(String, int, int, int) */
  public void setPlateAcquisitionAnnotationRef(String annotationRef, int plateIndex, int plateAcquisitionIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionAnnotationRef(annotationRef, plateIndex, plateAcquisitionIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionDescription(String, int, int) */
  public void setPlateAcquisitionDescription(String description, int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionDescription(description, plateIndex, plateAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionEndTime(String, int, int) */
  public void setPlateAcquisitionEndTime(String endTime, int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionEndTime(endTime, plateIndex, plateAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionID(String, int, int) */
  public void setPlateAcquisitionID(String id, int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionID(id, plateIndex, plateAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionMaximumFieldCount(Integer, int, int) */
  public void setPlateAcquisitionMaximumFieldCount(Integer maximumFieldCount, int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionMaximumFieldCount(maximumFieldCount, plateIndex, plateAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionName(String, int, int) */
  public void setPlateAcquisitionName(String name, int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionName(name, plateIndex, plateAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionStartTime(String, int, int) */
  public void setPlateAcquisitionStartTime(String startTime, int plateIndex, int plateAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionStartTime(startTime, plateIndex, plateAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateAcquisitionWellSampleRef(String, int, int, int) */
  public void setPlateAcquisitionWellSampleRef(String wellSampleRef, int plateIndex, int plateAcquisitionIndex, int wellSampleRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateAcquisitionWellSampleRef(wellSampleRef, plateIndex, plateAcquisitionIndex, wellSampleRefIndex);
      }
    }
  }

  // - PlateAcquisitionAnnotationRef property storage -

  // - PlateAnnotationRef property storage -

  // - PlateRef property storage -

  // - Point property storage -

  /* @see MetadataStore#setPointDescription(String, int, int) */
  public void setPointDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointFill(Integer, int, int) */
  public void setPointFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointFontSize(Integer, int, int) */
  public void setPointFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointID(String, int, int) */
  public void setPointID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointLabel(String, int, int) */
  public void setPointLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointName(String, int, int) */
  public void setPointName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointStroke(Integer, int, int) */
  public void setPointStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointStrokeDashArray(String, int, int) */
  public void setPointStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointStrokeWidth(Double, int, int) */
  public void setPointStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointTheC(Integer, int, int) */
  public void setPointTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointTheT(Integer, int, int) */
  public void setPointTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointTheZ(Integer, int, int) */
  public void setPointTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointTransform(String, int, int) */
  public void setPointTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointX(Double, int, int) */
  public void setPointX(Double x, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointX(x, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointY(Double, int, int) */
  public void setPointY(Double y, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointY(y, roiIndex, shapeIndex);
      }
    }
  }

  // - Polyline property storage -

  /* @see MetadataStore#setPolylineClosed(Boolean, int, int) */
  public void setPolylineClosed(Boolean closed, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineClosed(closed, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineDescription(String, int, int) */
  public void setPolylineDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineFill(Integer, int, int) */
  public void setPolylineFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineFontSize(Integer, int, int) */
  public void setPolylineFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineID(String, int, int) */
  public void setPolylineID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineLabel(String, int, int) */
  public void setPolylineLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineName(String, int, int) */
  public void setPolylineName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylinePoints(String, int, int) */
  public void setPolylinePoints(String points, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylinePoints(points, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineStroke(Integer, int, int) */
  public void setPolylineStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineStrokeDashArray(String, int, int) */
  public void setPolylineStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineStrokeWidth(Double, int, int) */
  public void setPolylineStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineTheC(Integer, int, int) */
  public void setPolylineTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineTheT(Integer, int, int) */
  public void setPolylineTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineTheZ(Integer, int, int) */
  public void setPolylineTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineTransform(String, int, int) */
  public void setPolylineTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  // - Project property storage -

  /* @see MetadataStore#setProjectAnnotationRef(String, int, int) */
  public void setProjectAnnotationRef(String annotationRef, int projectIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectAnnotationRef(annotationRef, projectIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setProjectDescription(String, int) */
  public void setProjectDescription(String description, int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectDescription(description, projectIndex);
      }
    }
  }

  /* @see MetadataStore#setProjectExperimenterRef(String, int) */
  public void setProjectExperimenterRef(String experimenterRef, int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectExperimenterRef(experimenterRef, projectIndex);
      }
    }
  }

  /* @see MetadataStore#setProjectGroupRef(String, int) */
  public void setProjectGroupRef(String groupRef, int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectGroupRef(groupRef, projectIndex);
      }
    }
  }

  /* @see MetadataStore#setProjectID(String, int) */
  public void setProjectID(String id, int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectID(id, projectIndex);
      }
    }
  }

  /* @see MetadataStore#setProjectName(String, int) */
  public void setProjectName(String name, int projectIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectName(name, projectIndex);
      }
    }
  }

  // - ProjectAnnotationRef property storage -

  // - ProjectRef property storage -

  // - ROI property storage -

  /* @see MetadataStore#setROIAnnotationRef(String, int, int) */
  public void setROIAnnotationRef(String annotationRef, int roiIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIAnnotationRef(annotationRef, roiIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setROIDescription(String, int) */
  public void setROIDescription(String description, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIDescription(description, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIID(String, int) */
  public void setROIID(String id, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIID(id, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIName(String, int) */
  public void setROIName(String name, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIName(name, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROINamespace(String, int) */
  public void setROINamespace(String namespace, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROINamespace(namespace, roiIndex);
      }
    }
  }

  // - ROIAnnotationRef property storage -

  // - Reagent property storage -

  /* @see MetadataStore#setReagentAnnotationRef(String, int, int, int) */
  public void setReagentAnnotationRef(String annotationRef, int screenIndex, int reagentIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setReagentAnnotationRef(annotationRef, screenIndex, reagentIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setReagentDescription(String, int, int) */
  public void setReagentDescription(String description, int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setReagentDescription(description, screenIndex, reagentIndex);
      }
    }
  }

  /* @see MetadataStore#setReagentID(String, int, int) */
  public void setReagentID(String id, int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setReagentID(id, screenIndex, reagentIndex);
      }
    }
  }

  /* @see MetadataStore#setReagentName(String, int, int) */
  public void setReagentName(String name, int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setReagentName(name, screenIndex, reagentIndex);
      }
    }
  }

  /* @see MetadataStore#setReagentReagentIdentifier(String, int, int) */
  public void setReagentReagentIdentifier(String reagentIdentifier, int screenIndex, int reagentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setReagentReagentIdentifier(reagentIdentifier, screenIndex, reagentIndex);
      }
    }
  }

  // - ReagentAnnotationRef property storage -

  // - Rectangle property storage -

  /* @see MetadataStore#setRectangleDescription(String, int, int) */
  public void setRectangleDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleFill(Integer, int, int) */
  public void setRectangleFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleFontSize(Integer, int, int) */
  public void setRectangleFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleHeight(Double, int, int) */
  public void setRectangleHeight(Double height, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleHeight(height, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleID(String, int, int) */
  public void setRectangleID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleLabel(String, int, int) */
  public void setRectangleLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleName(String, int, int) */
  public void setRectangleName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleStroke(Integer, int, int) */
  public void setRectangleStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleStrokeDashArray(String, int, int) */
  public void setRectangleStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleStrokeWidth(Double, int, int) */
  public void setRectangleStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleTheC(Integer, int, int) */
  public void setRectangleTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleTheT(Integer, int, int) */
  public void setRectangleTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleTheZ(Integer, int, int) */
  public void setRectangleTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleTransform(String, int, int) */
  public void setRectangleTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleWidth(Double, int, int) */
  public void setRectangleWidth(Double width, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleWidth(width, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleX(Double, int, int) */
  public void setRectangleX(Double x, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleX(x, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectangleY(Double, int, int) */
  public void setRectangleY(Double y, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectangleY(y, roiIndex, shapeIndex);
      }
    }
  }

  // - Screen property storage -

  /* @see MetadataStore#setScreenAnnotationRef(String, int, int) */
  public void setScreenAnnotationRef(String annotationRef, int screenIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenAnnotationRef(annotationRef, screenIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenDescription(String, int) */
  public void setScreenDescription(String description, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenDescription(description, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenID(String, int) */
  public void setScreenID(String id, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenID(id, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenName(String, int) */
  public void setScreenName(String name, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenName(name, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenPlateRef(String, int, int) */
  public void setScreenPlateRef(String plateRef, int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenPlateRef(plateRef, screenIndex, plateRefIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenProtocolDescription(String, int) */
  public void setScreenProtocolDescription(String protocolDescription, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenProtocolDescription(protocolDescription, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenProtocolIdentifier(String, int) */
  public void setScreenProtocolIdentifier(String protocolIdentifier, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenProtocolIdentifier(protocolIdentifier, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenReagentSetDescription(String, int) */
  public void setScreenReagentSetDescription(String reagentSetDescription, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenReagentSetDescription(reagentSetDescription, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenReagentSetIdentifier(String, int) */
  public void setScreenReagentSetIdentifier(String reagentSetIdentifier, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenReagentSetIdentifier(reagentSetIdentifier, screenIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenType(String, int) */
  public void setScreenType(String type, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenType(type, screenIndex);
      }
    }
  }

  // - ScreenAnnotationRef property storage -

  // - ScreenRef property storage -

  // - ShapeAnnotationRef property storage -

  // - StageLabel property storage -

  /* @see MetadataStore#setStageLabelName(String, int) */
  public void setStageLabelName(String name, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelName(name, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setStageLabelX(Double, int) */
  public void setStageLabelX(Double x, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelX(x, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setStageLabelY(Double, int) */
  public void setStageLabelY(Double y, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelY(y, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setStageLabelZ(Double, int) */
  public void setStageLabelZ(Double z, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelZ(z, imageIndex);
      }
    }
  }

  // - StringAnnotation property storage -

  /* @see MetadataStore#setStringAnnotationID(String, int) */
  public void setStringAnnotationID(String id, int stringAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStringAnnotationID(id, stringAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setStringAnnotationNamespace(String, int) */
  public void setStringAnnotationNamespace(String namespace, int stringAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStringAnnotationNamespace(namespace, stringAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setStringAnnotationValue(String, int) */
  public void setStringAnnotationValue(String value, int stringAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStringAnnotationValue(value, stringAnnotationIndex);
      }
    }
  }

  // - Text property storage -

  /* @see MetadataStore#setTextDescription(String, int, int) */
  public void setTextDescription(String description, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextDescription(description, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextFill(Integer, int, int) */
  public void setTextFill(Integer fill, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextFill(fill, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextFontSize(Integer, int, int) */
  public void setTextFontSize(Integer fontSize, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextFontSize(fontSize, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextID(String, int, int) */
  public void setTextID(String id, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextID(id, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextLabel(String, int, int) */
  public void setTextLabel(String label, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextLabel(label, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextName(String, int, int) */
  public void setTextName(String name, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextName(name, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextStroke(Integer, int, int) */
  public void setTextStroke(Integer stroke, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextStroke(stroke, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextStrokeDashArray(String, int, int) */
  public void setTextStrokeDashArray(String strokeDashArray, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextStrokeDashArray(strokeDashArray, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextStrokeWidth(Double, int, int) */
  public void setTextStrokeWidth(Double strokeWidth, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextStrokeWidth(strokeWidth, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextTheC(Integer, int, int) */
  public void setTextTheC(Integer theC, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextTheC(theC, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextTheT(Integer, int, int) */
  public void setTextTheT(Integer theT, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextTheT(theT, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextTheZ(Integer, int, int) */
  public void setTextTheZ(Integer theZ, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextTheZ(theZ, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextTransform(String, int, int) */
  public void setTextTransform(String transform, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextTransform(transform, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextValue(String, int, int) */
  public void setTextValue(String value, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextValue(value, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextX(Double, int, int) */
  public void setTextX(Double x, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextX(x, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setTextY(Double, int, int) */
  public void setTextY(Double y, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTextY(y, roiIndex, shapeIndex);
      }
    }
  }

  // - TiffData property storage -

  /* @see MetadataStore#setTiffDataFirstC(Integer, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFirstC(firstC, imageIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataFirstT(Integer, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFirstT(firstT, imageIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataFirstZ(Integer, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFirstZ(firstZ, imageIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataIFD(Integer, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataIFD(ifd, imageIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataPlaneCount(Integer, int, int) */
  public void setTiffDataPlaneCount(Integer planeCount, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataPlaneCount(planeCount, imageIndex, tiffDataIndex);
      }
    }
  }

  // - TimestampAnnotation property storage -

  /* @see MetadataStore#setTimestampAnnotationID(String, int) */
  public void setTimestampAnnotationID(String id, int timestampAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTimestampAnnotationID(id, timestampAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setTimestampAnnotationNamespace(String, int) */
  public void setTimestampAnnotationNamespace(String namespace, int timestampAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTimestampAnnotationNamespace(namespace, timestampAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setTimestampAnnotationValue(String, int) */
  public void setTimestampAnnotationValue(String value, int timestampAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTimestampAnnotationValue(value, timestampAnnotationIndex);
      }
    }
  }

  // - TransmittanceRange property storage -

  /* @see MetadataStore#setTransmittanceRangeCutIn(Integer, int, int) */
  public void setTransmittanceRangeCutIn(Integer cutIn, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTransmittanceRangeCutIn(cutIn, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setTransmittanceRangeCutInTolerance(Integer, int, int) */
  public void setTransmittanceRangeCutInTolerance(Integer cutInTolerance, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTransmittanceRangeCutInTolerance(cutInTolerance, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setTransmittanceRangeCutOut(Integer, int, int) */
  public void setTransmittanceRangeCutOut(Integer cutOut, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTransmittanceRangeCutOut(cutOut, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setTransmittanceRangeCutOutTolerance(Integer, int, int) */
  public void setTransmittanceRangeCutOutTolerance(Integer cutOutTolerance, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTransmittanceRangeCutOutTolerance(cutOutTolerance, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setTransmittanceRangeTransmittance(PercentFraction, int, int) */
  public void setTransmittanceRangeTransmittance(PercentFraction transmittance, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
      }
    }
  }

  // - UUID property storage -
  
  public void setUUIDValue(String value, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setUUIDValue(value, imageIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setUUIDFileName(String, int, int) */
  public void setUUIDFileName(String fileName, int imageIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setUUIDFileName(fileName, imageIndex, tiffDataIndex);
      }
    }
  }

  // - Well property storage -

  /* @see MetadataStore#setWellAnnotationRef(String, int, int, int) */
  public void setWellAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellAnnotationRef(annotationRef, plateIndex, wellIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setWellColor(Integer, int, int) */
  public void setWellColor(Integer color, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellColor(color, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellColumn(NonNegativeInteger, int, int) */
  public void setWellColumn(NonNegativeInteger column, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellColumn(column, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellExternalDescription(String, int, int) */
  public void setWellExternalDescription(String externalDescription, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellExternalDescription(externalDescription, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellExternalIdentifier(String, int, int) */
  public void setWellExternalIdentifier(String externalIdentifier, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellExternalIdentifier(externalIdentifier, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellID(String, int, int) */
  public void setWellID(String id, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellID(id, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellReagentRef(String, int, int) */
  public void setWellReagentRef(String reagentRef, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellReagentRef(reagentRef, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellRow(NonNegativeInteger, int, int) */
  public void setWellRow(NonNegativeInteger row, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellRow(row, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellStatus(String, int, int) */
  public void setWellStatus(String status, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellStatus(status, plateIndex, wellIndex);
      }
    }
  }

  // - WellAnnotationRef property storage -

  // - WellSample property storage -

  /* @see MetadataStore#setWellSampleAnnotationRef(String, int, int, int, int) */
  public void setWellSampleAnnotationRef(String annotationRef, int plateIndex, int wellIndex, int wellSampleIndex, int annotationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleAnnotationRef(annotationRef, plateIndex, wellIndex, wellSampleIndex, annotationRefIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSampleID(String, int, int, int) */
  public void setWellSampleID(String id, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleID(id, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSampleImageRef(String, int, int, int) */
  public void setWellSampleImageRef(String imageRef, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleImageRef(imageRef, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSampleIndex(NonNegativeInteger, int, int, int) */
  public void setWellSampleIndex(NonNegativeInteger index, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSamplePositionX(Double, int, int, int) */
  public void setWellSamplePositionX(Double positionX, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSamplePositionX(positionX, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSamplePositionY(Double, int, int, int) */
  public void setWellSamplePositionY(Double positionY, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSamplePositionY(positionY, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSampleTimepoint(Integer, int, int, int) */
  public void setWellSampleTimepoint(Integer timepoint, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleTimepoint(timepoint, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  // - WellSampleAnnotationRef property storage -

  // - WellSampleRef property storage -

  // - XMLAnnotation property storage -

  /* @see MetadataStore#setXMLAnnotationID(String, int) */
  public void setXMLAnnotationID(String id, int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setXMLAnnotationID(id, xmlAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setXMLAnnotationNamespace(String, int) */
  public void setXMLAnnotationNamespace(String namespace, int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setXMLAnnotationNamespace(namespace, xmlAnnotationIndex);
      }
    }
  }

  /* @see MetadataStore#setXMLAnnotationValue(String, int) */
  public void setXMLAnnotationValue(String value, int xmlAnnotationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setXMLAnnotationValue(value, xmlAnnotationIndex);
      }
    }
  }

}
