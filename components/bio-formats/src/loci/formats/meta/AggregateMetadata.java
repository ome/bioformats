//
// AggregateMetadata.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-2009 UW-Madison LOCI and Glencoe Software, Inc.

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
 * Created by melissa via MetadataAutogen on Oct 5, 2009 9:38:21 AM CDT
 *
 *-----------------------------------------------------------------------------
 */

package loci.formats.meta;

import java.util.Iterator;
import java.util.List;

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

  /* @see MetadataRetrieve#getChannelComponentCount(int, int) */
  public int getChannelComponentCount(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getChannelComponentCount(imageIndex, logicalChannelIndex);
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

  /* @see MetadataRetrieve#getExperimenterMembershipCount(int) */
  public int getExperimenterMembershipCount(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getExperimenterMembershipCount(experimenterIndex);
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

  /* @see MetadataRetrieve#getGroupRefCount(int) */
  public int getGroupRefCount(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getGroupRefCount(experimenterIndex);
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

  /* @see MetadataRetrieve#getLightSourceCount(int) */
  public int getLightSourceCount(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getLightSourceCount(instrumentIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getLightSourceRefCount(int, int) */
  public int getLightSourceRefCount(int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getLightSourceRefCount(imageIndex, microbeamManipulationIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getLogicalChannelCount(int) */
  public int getLogicalChannelCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getLogicalChannelCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationCount(int) */
  public int getMicrobeamManipulationCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getMicrobeamManipulationCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationRefCount(int) */
  public int getMicrobeamManipulationRefCount(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getMicrobeamManipulationRefCount(experimentIndex);
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

  /* @see MetadataRetrieve#getPixelsCount(int) */
  public int getPixelsCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPixelsCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getPlaneCount(int, int) */
  public int getPlaneCount(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getPlaneCount(imageIndex, pixelsIndex);
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

  /* @see MetadataRetrieve#getROICount(int) */
  public int getROICount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getROICount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getROIRefCount(int, int) */
  public int getROIRefCount(int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getROIRefCount(imageIndex, microbeamManipulationIndex);
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

  /* @see MetadataRetrieve#getRegionCount(int) */
  public int getRegionCount(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getRegionCount(imageIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getRoiLinkCount(int, int) */
  public int getRoiLinkCount(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getRoiLinkCount(imageIndex, roiIndex);
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

  /* @see MetadataRetrieve#getScreenAcquisitionCount(int) */
  public int getScreenAcquisitionCount(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getScreenAcquisitionCount(screenIndex);
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

  /* @see MetadataRetrieve#getShapeCount(int, int) */
  public int getShapeCount(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getShapeCount(imageIndex, roiIndex);
        if (result >= 0) return result;
      }
    }
    return -1;
  }

  /* @see MetadataRetrieve#getTiffDataCount(int, int) */
  public int getTiffDataCount(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getTiffDataCount(imageIndex, pixelsIndex);
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

  /* @see MetadataRetrieve#getWellSampleRefCount(int, int) */
  public int getWellSampleRefCount(int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        int result = retrieve.getWellSampleRefCount(screenIndex, screenAcquisitionIndex);
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

  /* @see MetadataRetrieve#getArcType(int, int) */
  public String getArcType(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getArcType(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ChannelComponent property retrieval -

  /* @see MetadataRetrieve#getChannelComponentColorDomain(int, int, int) */
  public String getChannelComponentColorDomain(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelComponentColorDomain(imageIndex, logicalChannelIndex, channelComponentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelComponentIndex(int, int, int) */
  public Integer getChannelComponentIndex(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getChannelComponentIndex(imageIndex, logicalChannelIndex, channelComponentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getChannelComponentPixels(int, int, int) */
  public String getChannelComponentPixels(int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getChannelComponentPixels(imageIndex, logicalChannelIndex, channelComponentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Circle property retrieval -

  /* @see MetadataRetrieve#getCircleCx(int, int, int) */
  public String getCircleCx(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getCircleCx(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getCircleCy(int, int, int) */
  public String getCircleCy(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getCircleCy(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getCircleID(int, int, int) */
  public String getCircleID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getCircleID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getCircleR(int, int, int) */
  public String getCircleR(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getCircleR(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getCircleTransform(int, int, int) */
  public String getCircleTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getCircleTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Contact property retrieval -

  /* @see MetadataRetrieve#getContactExperimenter(int) */
  public String getContactExperimenter(int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getContactExperimenter(groupIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Dataset property retrieval -

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

  /* @see MetadataRetrieve#getDatasetLocked(int) */
  public Boolean getDatasetLocked(int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getDatasetLocked(datasetIndex);
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

  // - DatasetRef property retrieval -

  /* @see MetadataRetrieve#getDatasetRefID(int, int) */
  public String getDatasetRefID(int imageIndex, int datasetRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDatasetRefID(imageIndex, datasetRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Detector property retrieval -

  /* @see MetadataRetrieve#getDetectorAmplificationGain(int, int) */
  public Float getDetectorAmplificationGain(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorAmplificationGain(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorGain(int, int) */
  public Float getDetectorGain(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorGain(instrumentIndex, detectorIndex);
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
  public Float getDetectorOffset(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorOffset(instrumentIndex, detectorIndex);
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
  public String getDetectorType(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorType(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorVoltage(int, int) */
  public Float getDetectorVoltage(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorVoltage(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorZoom(int, int) */
  public Float getDetectorZoom(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorZoom(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - DetectorSettings property retrieval -

  /* @see MetadataRetrieve#getDetectorSettingsBinning(int, int) */
  public String getDetectorSettingsBinning(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorSettingsBinning(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsDetector(int, int) */
  public String getDetectorSettingsDetector(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDetectorSettingsDetector(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsGain(int, int) */
  public Float getDetectorSettingsGain(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorSettingsGain(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsOffset(int, int) */
  public Float getDetectorSettingsOffset(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorSettingsOffset(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsReadOutRate(int, int) */
  public Float getDetectorSettingsReadOutRate(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorSettingsReadOutRate(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettingsVoltage(int, int) */
  public Float getDetectorSettingsVoltage(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDetectorSettingsVoltage(imageIndex, logicalChannelIndex);
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

  // - Dimensions property retrieval -

  /* @see MetadataRetrieve#getDimensionsPhysicalSizeX(int, int) */
  public Float getDimensionsPhysicalSizeX(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDimensionsPhysicalSizeX(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDimensionsPhysicalSizeY(int, int) */
  public Float getDimensionsPhysicalSizeY(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDimensionsPhysicalSizeY(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDimensionsPhysicalSizeZ(int, int) */
  public Float getDimensionsPhysicalSizeZ(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDimensionsPhysicalSizeZ(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDimensionsTimeIncrement(int, int) */
  public Float getDimensionsTimeIncrement(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDimensionsTimeIncrement(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDimensionsWaveIncrement(int, int) */
  public Integer getDimensionsWaveIncrement(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getDimensionsWaveIncrement(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDimensionsWaveStart(int, int) */
  public Integer getDimensionsWaveStart(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getDimensionsWaveStart(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - DisplayOptions property retrieval -

  /* @see MetadataRetrieve#getDisplayOptionsDisplay(int) */
  public String getDisplayOptionsDisplay(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDisplayOptionsDisplay(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDisplayOptionsID(int) */
  public String getDisplayOptionsID(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getDisplayOptionsID(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDisplayOptionsZoom(int) */
  public Float getDisplayOptionsZoom(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getDisplayOptionsZoom(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Ellipse property retrieval -

  /* @see MetadataRetrieve#getEllipseCx(int, int, int) */
  public String getEllipseCx(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseCx(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseCy(int, int, int) */
  public String getEllipseCy(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseCy(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseID(int, int, int) */
  public String getEllipseID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseRx(int, int, int) */
  public String getEllipseRx(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseRx(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseRy(int, int, int) */
  public String getEllipseRy(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseRy(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEllipseTransform(int, int, int) */
  public String getEllipseTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEllipseTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - EmFilter property retrieval -

  /* @see MetadataRetrieve#getEmFilterLotNumber(int, int) */
  public String getEmFilterLotNumber(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEmFilterLotNumber(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEmFilterManufacturer(int, int) */
  public String getEmFilterManufacturer(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEmFilterManufacturer(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEmFilterModel(int, int) */
  public String getEmFilterModel(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEmFilterModel(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getEmFilterType(int, int) */
  public String getEmFilterType(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getEmFilterType(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ExFilter property retrieval -

  /* @see MetadataRetrieve#getExFilterLotNumber(int, int) */
  public String getExFilterLotNumber(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExFilterLotNumber(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExFilterManufacturer(int, int) */
  public String getExFilterManufacturer(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExFilterManufacturer(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExFilterModel(int, int) */
  public String getExFilterModel(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExFilterModel(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExFilterType(int, int) */
  public String getExFilterType(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExFilterType(instrumentIndex, filterIndex);
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
  public String getExperimentType(int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimentType(experimentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Experimenter property retrieval -

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

  /* @see MetadataRetrieve#getExperimenterOMEName(int) */
  public String getExperimenterOMEName(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterOMEName(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ExperimenterMembership property retrieval -

  /* @see MetadataRetrieve#getExperimenterMembershipGroup(int, int) */
  public String getExperimenterMembershipGroup(int experimenterIndex, int groupRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterMembershipGroup(experimenterIndex, groupRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Filament property retrieval -

  /* @see MetadataRetrieve#getFilamentType(int, int) */
  public String getFilamentType(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilamentType(instrumentIndex, lightSourceIndex);
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

  /* @see MetadataRetrieve#getFilterType(int, int) */
  public String getFilterType(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterType(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - FilterSet property retrieval -

  /* @see MetadataRetrieve#getFilterSetDichroic(int, int) */
  public String getFilterSetDichroic(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetDichroic(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetEmFilter(int, int) */
  public String getFilterSetEmFilter(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetEmFilter(instrumentIndex, filterSetIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilterSetExFilter(int, int) */
  public String getFilterSetExFilter(int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getFilterSetExFilter(instrumentIndex, filterSetIndex);
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

  // - Group property retrieval -

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

  // - GroupRef property retrieval -

  // - Image property retrieval -

  /* @see MetadataRetrieve#getImageAcquiredPixels(int) */
  public String getImageAcquiredPixels(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageAcquiredPixels(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageCreationDate(int) */
  public String getImageCreationDate(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageCreationDate(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImageDefaultPixels(int) */
  public String getImageDefaultPixels(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getImageDefaultPixels(imageIndex);
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

  // - ImagingEnvironment property retrieval -

  /* @see MetadataRetrieve#getImagingEnvironmentAirPressure(int) */
  public Float getImagingEnvironmentAirPressure(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getImagingEnvironmentAirPressure(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironmentCO2Percent(int) */
  public Float getImagingEnvironmentCO2Percent(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getImagingEnvironmentCO2Percent(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironmentHumidity(int) */
  public Float getImagingEnvironmentHumidity(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getImagingEnvironmentHumidity(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironmentTemperature(int) */
  public Float getImagingEnvironmentTemperature(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getImagingEnvironmentTemperature(imageIndex);
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
  public Integer getLaserFrequencyMultiplication(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLaserFrequencyMultiplication(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserLaserMedium(int, int) */
  public String getLaserLaserMedium(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserLaserMedium(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserPockelCell(int, int) */
  public Boolean getLaserPockelCell(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getLaserPockelCell(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserPulse(int, int) */
  public String getLaserPulse(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserPulse(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserRepetitionRate(int, int) */
  public Boolean getLaserRepetitionRate(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getLaserRepetitionRate(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserTuneable(int, int) */
  public Boolean getLaserTuneable(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getLaserTuneable(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserType(int, int) */
  public String getLaserType(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLaserType(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaserWavelength(int, int) */
  public Integer getLaserWavelength(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLaserWavelength(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LightSource property retrieval -

  /* @see MetadataRetrieve#getLightSourceID(int, int) */
  public String getLightSourceID(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightSourceID(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceManufacturer(int, int) */
  public String getLightSourceManufacturer(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightSourceManufacturer(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceModel(int, int) */
  public String getLightSourceModel(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightSourceModel(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourcePower(int, int) */
  public Float getLightSourcePower(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getLightSourcePower(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceSerialNumber(int, int) */
  public String getLightSourceSerialNumber(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightSourceSerialNumber(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LightSourceRef property retrieval -

  /* @see MetadataRetrieve#getLightSourceRefAttenuation(int, int, int) */
  public Float getLightSourceRefAttenuation(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getLightSourceRefAttenuation(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceRefLightSource(int, int, int) */
  public String getLightSourceRefLightSource(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightSourceRefLightSource(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceRefWavelength(int, int, int) */
  public Integer getLightSourceRefWavelength(int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLightSourceRefWavelength(imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LightSourceSettings property retrieval -

  /* @see MetadataRetrieve#getLightSourceSettingsAttenuation(int, int) */
  public Float getLightSourceSettingsAttenuation(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getLightSourceSettingsAttenuation(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceSettingsLightSource(int, int) */
  public String getLightSourceSettingsLightSource(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLightSourceSettingsLightSource(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceSettingsWavelength(int, int) */
  public Integer getLightSourceSettingsWavelength(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLightSourceSettingsWavelength(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Line property retrieval -

  /* @see MetadataRetrieve#getLineID(int, int, int) */
  public String getLineID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineTransform(int, int, int) */
  public String getLineTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineX1(int, int, int) */
  public String getLineX1(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineX1(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineX2(int, int, int) */
  public String getLineX2(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineX2(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineY1(int, int, int) */
  public String getLineY1(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineY1(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLineY2(int, int, int) */
  public String getLineY2(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLineY2(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - LogicalChannel property retrieval -

  /* @see MetadataRetrieve#getLogicalChannelContrastMethod(int, int) */
  public String getLogicalChannelContrastMethod(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelContrastMethod(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelDetector(int, int) */
  public String getLogicalChannelDetector(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelDetector(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelEmWave(int, int) */
  public Integer getLogicalChannelEmWave(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLogicalChannelEmWave(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelExWave(int, int) */
  public Integer getLogicalChannelExWave(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLogicalChannelExWave(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelFilterSet(int, int) */
  public String getLogicalChannelFilterSet(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelFilterSet(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelFluor(int, int) */
  public String getLogicalChannelFluor(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelFluor(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelID(int, int) */
  public String getLogicalChannelID(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelID(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelIlluminationType(int, int) */
  public String getLogicalChannelIlluminationType(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelIlluminationType(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelLightSource(int, int) */
  public String getLogicalChannelLightSource(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelLightSource(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelMode(int, int) */
  public String getLogicalChannelMode(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelMode(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelName(int, int) */
  public String getLogicalChannelName(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelName(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelNdFilter(int, int) */
  public Float getLogicalChannelNdFilter(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getLogicalChannelNdFilter(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelOTF(int, int) */
  public String getLogicalChannelOTF(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelOTF(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelPhotometricInterpretation(int, int) */
  public String getLogicalChannelPhotometricInterpretation(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelPhotometricInterpretation(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelPinholeSize(int, int) */
  public Float getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelPockelCellSetting(int, int) */
  public Integer getLogicalChannelPockelCellSetting(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLogicalChannelPockelCellSetting(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelSamplesPerPixel(int, int) */
  public Integer getLogicalChannelSamplesPerPixel(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLogicalChannelSamplesPerPixel(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelSecondaryEmissionFilter(int, int) */
  public String getLogicalChannelSecondaryEmissionFilter(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelSecondaryEmissionFilter(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannelSecondaryExcitationFilter(int, int) */
  public String getLogicalChannelSecondaryExcitationFilter(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getLogicalChannelSecondaryExcitationFilter(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Mask property retrieval -

  /* @see MetadataRetrieve#getMaskHeight(int, int, int) */
  public String getMaskHeight(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskHeight(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskID(int, int, int) */
  public String getMaskID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskTransform(int, int, int) */
  public String getMaskTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskWidth(int, int, int) */
  public String getMaskWidth(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskWidth(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskX(int, int, int) */
  public String getMaskX(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskX(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskY(int, int, int) */
  public String getMaskY(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskY(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - MaskPixels property retrieval -

  /* @see MetadataRetrieve#getMaskPixelsBigEndian(int, int, int) */
  public Boolean getMaskPixelsBigEndian(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getMaskPixelsBigEndian(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskPixelsBinData(int, int, int) */
  public byte[] getMaskPixelsBinData(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        byte[] result = retrieve.getMaskPixelsBinData(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskPixelsExtendedPixelType(int, int, int) */
  public String getMaskPixelsExtendedPixelType(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskPixelsExtendedPixelType(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskPixelsID(int, int, int) */
  public String getMaskPixelsID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMaskPixelsID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskPixelsSizeX(int, int, int) */
  public Integer getMaskPixelsSizeX(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskPixelsSizeX(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMaskPixelsSizeY(int, int, int) */
  public Integer getMaskPixelsSizeY(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getMaskPixelsSizeY(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - MicrobeamManipulation property retrieval -

  /* @see MetadataRetrieve#getMicrobeamManipulationExperimenterRef(int, int) */
  public String getMicrobeamManipulationExperimenterRef(int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationExperimenterRef(imageIndex, microbeamManipulationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationID(int, int) */
  public String getMicrobeamManipulationID(int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationID(imageIndex, microbeamManipulationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getMicrobeamManipulationType(int, int) */
  public String getMicrobeamManipulationType(int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationType(imageIndex, microbeamManipulationIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - MicrobeamManipulationRef property retrieval -

  /* @see MetadataRetrieve#getMicrobeamManipulationRefID(int, int) */
  public String getMicrobeamManipulationRefID(int experimentIndex, int microbeamManipulationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicrobeamManipulationRefID(experimentIndex, microbeamManipulationRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Microscope property retrieval -

  /* @see MetadataRetrieve#getMicroscopeID(int) */
  public String getMicroscopeID(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicroscopeID(instrumentIndex);
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
  public String getMicroscopeType(int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getMicroscopeType(instrumentIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - OTF property retrieval -

  /* @see MetadataRetrieve#getOTFBinaryFile(int, int) */
  public String getOTFBinaryFile(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFBinaryFile(instrumentIndex, otfIndex);
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

  /* @see MetadataRetrieve#getOTFObjective(int, int) */
  public String getOTFObjective(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFObjective(instrumentIndex, otfIndex);
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

  /* @see MetadataRetrieve#getOTFPixelType(int, int) */
  public String getOTFPixelType(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFPixelType(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFSizeX(int, int) */
  public Integer getOTFSizeX(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getOTFSizeX(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTFSizeY(int, int) */
  public Integer getOTFSizeY(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getOTFSizeY(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Objective property retrieval -

  /* @see MetadataRetrieve#getObjectiveCalibratedMagnification(int, int) */
  public Float getObjectiveCalibratedMagnification(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getObjectiveCalibratedMagnification(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveCorrection(int, int) */
  public String getObjectiveCorrection(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveCorrection(instrumentIndex, objectiveIndex);
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
  public String getObjectiveImmersion(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveImmersion(instrumentIndex, objectiveIndex);
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
  public Float getObjectiveLensNA(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getObjectiveLensNA(instrumentIndex, objectiveIndex);
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
  public Float getObjectiveWorkingDistance(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getObjectiveWorkingDistance(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ObjectiveSettings property retrieval -

  /* @see MetadataRetrieve#getObjectiveSettingsCorrectionCollar(int) */
  public Float getObjectiveSettingsCorrectionCollar(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getObjectiveSettingsCorrectionCollar(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveSettingsMedium(int) */
  public String getObjectiveSettingsMedium(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveSettingsMedium(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveSettingsObjective(int) */
  public String getObjectiveSettingsObjective(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getObjectiveSettingsObjective(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjectiveSettingsRefractiveIndex(int) */
  public Float getObjectiveSettingsRefractiveIndex(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getObjectiveSettingsRefractiveIndex(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Path property retrieval -

  /* @see MetadataRetrieve#getPathD(int, int, int) */
  public String getPathD(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathD(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPathID(int, int, int) */
  public String getPathID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPathID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Pixels property retrieval -

  /* @see MetadataRetrieve#getPixelsBigEndian(int, int) */
  public Boolean getPixelsBigEndian(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getPixelsBigEndian(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsDimensionOrder(int, int) */
  public String getPixelsDimensionOrder(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPixelsDimensionOrder(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsID(int, int) */
  public String getPixelsID(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPixelsID(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsPixelType(int, int) */
  public String getPixelsPixelType(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPixelsPixelType(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeC(int, int) */
  public Integer getPixelsSizeC(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPixelsSizeC(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeT(int, int) */
  public Integer getPixelsSizeT(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPixelsSizeT(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeX(int, int) */
  public Integer getPixelsSizeX(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPixelsSizeX(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeY(int, int) */
  public Integer getPixelsSizeY(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPixelsSizeY(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixelsSizeZ(int, int) */
  public Integer getPixelsSizeZ(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPixelsSizeZ(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Plane property retrieval -

  /* @see MetadataRetrieve#getPlaneHashSHA1(int, int, int) */
  public String getPlaneHashSHA1(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlaneHashSHA1(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneID(int, int, int) */
  public String getPlaneID(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlaneID(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTheC(int, int, int) */
  public Integer getPlaneTheC(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlaneTheC(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTheT(int, int, int) */
  public Integer getPlaneTheT(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlaneTheT(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTheZ(int, int, int) */
  public Integer getPlaneTheZ(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlaneTheZ(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - PlaneTiming property retrieval -

  /* @see MetadataRetrieve#getPlaneTimingDeltaT(int, int, int) */
  public Float getPlaneTimingDeltaT(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getPlaneTimingDeltaT(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTimingExposureTime(int, int, int) */
  public Float getPlaneTimingExposureTime(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getPlaneTimingExposureTime(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Plate property retrieval -

  /* @see MetadataRetrieve#getPlateColumnNamingConvention(int) */
  public String getPlateColumnNamingConvention(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateColumnNamingConvention(plateIndex);
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
  public String getPlateRowNamingConvention(int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateRowNamingConvention(plateIndex);
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

  // - PlateRef property retrieval -

  /* @see MetadataRetrieve#getPlateRefID(int, int) */
  public String getPlateRefID(int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateRefID(screenIndex, plateRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateRefSample(int, int) */
  public Integer getPlateRefSample(int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getPlateRefSample(screenIndex, plateRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlateRefWell(int, int) */
  public String getPlateRefWell(int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPlateRefWell(screenIndex, plateRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Point property retrieval -

  /* @see MetadataRetrieve#getPointCx(int, int, int) */
  public String getPointCx(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointCx(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointCy(int, int, int) */
  public String getPointCy(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointCy(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointID(int, int, int) */
  public String getPointID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointR(int, int, int) */
  public String getPointR(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointR(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPointTransform(int, int, int) */
  public String getPointTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPointTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Polygon property retrieval -

  /* @see MetadataRetrieve#getPolygonID(int, int, int) */
  public String getPolygonID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolygonID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolygonPoints(int, int, int) */
  public String getPolygonPoints(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolygonPoints(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolygonTransform(int, int, int) */
  public String getPolygonTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolygonTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Polyline property retrieval -

  /* @see MetadataRetrieve#getPolylineID(int, int, int) */
  public String getPolylineID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylinePoints(int, int, int) */
  public String getPolylinePoints(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylinePoints(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPolylineTransform(int, int, int) */
  public String getPolylineTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPolylineTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Project property retrieval -

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

  // - ProjectRef property retrieval -

  /* @see MetadataRetrieve#getProjectRefID(int, int) */
  public String getProjectRefID(int datasetIndex, int projectRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getProjectRefID(datasetIndex, projectRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Pump property retrieval -

  /* @see MetadataRetrieve#getPumpLightSource(int, int) */
  public String getPumpLightSource(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getPumpLightSource(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ROI property retrieval -

  /* @see MetadataRetrieve#getROIID(int, int) */
  public String getROIID(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROIID(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIT0(int, int) */
  public Integer getROIT0(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIT0(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIT1(int, int) */
  public Integer getROIT1(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIT1(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIX0(int, int) */
  public Integer getROIX0(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIX0(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIX1(int, int) */
  public Integer getROIX1(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIX1(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIY0(int, int) */
  public Integer getROIY0(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIY0(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIY1(int, int) */
  public Integer getROIY1(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIY1(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIZ0(int, int) */
  public Integer getROIZ0(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIZ0(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROIZ1(int, int) */
  public Integer getROIZ1(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getROIZ1(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ROIRef property retrieval -

  /* @see MetadataRetrieve#getROIRefID(int, int, int) */
  public String getROIRefID(int imageIndex, int microbeamManipulationIndex, int roiRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getROIRefID(imageIndex, microbeamManipulationIndex, roiRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Reagent property retrieval -

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

  // - Rect property retrieval -

  /* @see MetadataRetrieve#getRectHeight(int, int, int) */
  public String getRectHeight(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectHeight(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectID(int, int, int) */
  public String getRectID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectTransform(int, int, int) */
  public String getRectTransform(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectTransform(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectWidth(int, int, int) */
  public String getRectWidth(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectWidth(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectX(int, int, int) */
  public String getRectX(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectX(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRectY(int, int, int) */
  public String getRectY(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRectY(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Region property retrieval -

  /* @see MetadataRetrieve#getRegionID(int, int) */
  public String getRegionID(int imageIndex, int regionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRegionID(imageIndex, regionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRegionName(int, int) */
  public String getRegionName(int imageIndex, int regionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRegionName(imageIndex, regionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRegionTag(int, int) */
  public String getRegionTag(int imageIndex, int regionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRegionTag(imageIndex, regionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - RoiLink property retrieval -

  /* @see MetadataRetrieve#getRoiLinkDirection(int, int, int) */
  public String getRoiLinkDirection(int imageIndex, int roiIndex, int roiLinkIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRoiLinkDirection(imageIndex, roiIndex, roiLinkIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRoiLinkName(int, int, int) */
  public String getRoiLinkName(int imageIndex, int roiIndex, int roiLinkIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRoiLinkName(imageIndex, roiIndex, roiLinkIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getRoiLinkRef(int, int, int) */
  public String getRoiLinkRef(int imageIndex, int roiIndex, int roiLinkIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getRoiLinkRef(imageIndex, roiIndex, roiLinkIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Screen property retrieval -

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

  /* @see MetadataRetrieve#getScreenExtern(int) */
  public String getScreenExtern(int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenExtern(screenIndex);
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

  // - ScreenAcquisition property retrieval -

  /* @see MetadataRetrieve#getScreenAcquisitionEndTime(int, int) */
  public String getScreenAcquisitionEndTime(int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenAcquisitionEndTime(screenIndex, screenAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenAcquisitionID(int, int) */
  public String getScreenAcquisitionID(int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenAcquisitionID(screenIndex, screenAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getScreenAcquisitionStartTime(int, int) */
  public String getScreenAcquisitionStartTime(int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenAcquisitionStartTime(screenIndex, screenAcquisitionIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - ScreenRef property retrieval -

  /* @see MetadataRetrieve#getScreenRefID(int, int) */
  public String getScreenRefID(int plateIndex, int screenRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getScreenRefID(plateIndex, screenRefIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Shape property retrieval -

  /* @see MetadataRetrieve#getShapeBaselineShift(int, int, int) */
  public String getShapeBaselineShift(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeBaselineShift(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeDirection(int, int, int) */
  public String getShapeDirection(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeDirection(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFillColor(int, int, int) */
  public String getShapeFillColor(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFillColor(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFillOpacity(int, int, int) */
  public String getShapeFillOpacity(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFillOpacity(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFillRule(int, int, int) */
  public String getShapeFillRule(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFillRule(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFontFamily(int, int, int) */
  public String getShapeFontFamily(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFontFamily(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFontSize(int, int, int) */
  public Integer getShapeFontSize(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getShapeFontSize(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFontStretch(int, int, int) */
  public String getShapeFontStretch(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFontStretch(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFontStyle(int, int, int) */
  public String getShapeFontStyle(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFontStyle(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFontVariant(int, int, int) */
  public String getShapeFontVariant(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFontVariant(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeFontWeight(int, int, int) */
  public String getShapeFontWeight(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeFontWeight(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeG(int, int, int) */
  public String getShapeG(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeG(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeGlyphOrientationVertical(int, int, int) */
  public Integer getShapeGlyphOrientationVertical(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getShapeGlyphOrientationVertical(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeID(int, int, int) */
  public String getShapeID(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeID(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeLocked(int, int, int) */
  public Boolean getShapeLocked(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getShapeLocked(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeAttribute(int, int, int) */
  public String getShapeStrokeAttribute(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeStrokeAttribute(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeColor(int, int, int) */
  public String getShapeStrokeColor(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeStrokeColor(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeDashArray(int, int, int) */
  public String getShapeStrokeDashArray(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeStrokeDashArray(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeLineCap(int, int, int) */
  public String getShapeStrokeLineCap(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeStrokeLineCap(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeLineJoin(int, int, int) */
  public String getShapeStrokeLineJoin(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeStrokeLineJoin(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeMiterLimit(int, int, int) */
  public Integer getShapeStrokeMiterLimit(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getShapeStrokeMiterLimit(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeOpacity(int, int, int) */
  public Float getShapeStrokeOpacity(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getShapeStrokeOpacity(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeStrokeWidth(int, int, int) */
  public Integer getShapeStrokeWidth(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getShapeStrokeWidth(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeText(int, int, int) */
  public String getShapeText(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeText(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeTextAnchor(int, int, int) */
  public String getShapeTextAnchor(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeTextAnchor(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeTextDecoration(int, int, int) */
  public String getShapeTextDecoration(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeTextDecoration(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeTextFill(int, int, int) */
  public String getShapeTextFill(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeTextFill(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeTextStroke(int, int, int) */
  public String getShapeTextStroke(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeTextStroke(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeTheT(int, int, int) */
  public Integer getShapeTheT(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getShapeTheT(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeTheZ(int, int, int) */
  public Integer getShapeTheZ(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getShapeTheZ(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeVectorEffect(int, int, int) */
  public String getShapeVectorEffect(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeVectorEffect(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeVisibility(int, int, int) */
  public Boolean getShapeVisibility(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Boolean result = retrieve.getShapeVisibility(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getShapeWritingMode(int, int, int) */
  public String getShapeWritingMode(int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getShapeWritingMode(imageIndex, roiIndex, shapeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

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
  public Float getStageLabelX(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getStageLabelX(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStageLabelY(int) */
  public Float getStageLabelY(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getStageLabelY(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStageLabelZ(int) */
  public Float getStageLabelZ(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getStageLabelZ(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - StagePosition property retrieval -

  /* @see MetadataRetrieve#getStagePositionPositionX(int, int, int) */
  public Float getStagePositionPositionX(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getStagePositionPositionX(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStagePositionPositionY(int, int, int) */
  public Float getStagePositionPositionY(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getStagePositionPositionY(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStagePositionPositionZ(int, int, int) */
  public Float getStagePositionPositionZ(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getStagePositionPositionZ(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Thumbnail property retrieval -

  /* @see MetadataRetrieve#getThumbnailHref(int) */
  public String getThumbnailHref(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getThumbnailHref(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getThumbnailID(int) */
  public String getThumbnailID(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getThumbnailID(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getThumbnailMIMEtype(int) */
  public String getThumbnailMIMEtype(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getThumbnailMIMEtype(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - TiffData property retrieval -

  /* @see MetadataRetrieve#getTiffDataFileName(int, int, int) */
  public String getTiffDataFileName(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTiffDataFileName(imageIndex, pixelsIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataFirstC(int, int, int) */
  public Integer getTiffDataFirstC(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataFirstC(imageIndex, pixelsIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataFirstT(int, int, int) */
  public Integer getTiffDataFirstT(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataFirstT(imageIndex, pixelsIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataFirstZ(int, int, int) */
  public Integer getTiffDataFirstZ(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataFirstZ(imageIndex, pixelsIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataIFD(int, int, int) */
  public Integer getTiffDataIFD(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataIFD(imageIndex, pixelsIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataNumPlanes(int, int, int) */
  public Integer getTiffDataNumPlanes(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTiffDataNumPlanes(imageIndex, pixelsIndex, tiffDataIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getTiffDataUUID(int, int, int) */
  public String getTiffDataUUID(int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getTiffDataUUID(imageIndex, pixelsIndex, tiffDataIndex);
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
  public Integer getTransmittanceRangeTransmittance(int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getTransmittanceRangeTransmittance(instrumentIndex, filterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Well property retrieval -

  /* @see MetadataRetrieve#getWellColumn(int, int) */
  public Integer getWellColumn(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getWellColumn(plateIndex, wellIndex);
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

  /* @see MetadataRetrieve#getWellReagent(int, int) */
  public String getWellReagent(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellReagent(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellRow(int, int) */
  public Integer getWellRow(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getWellRow(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellType(int, int) */
  public String getWellType(int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellType(plateIndex, wellIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - WellSample property retrieval -

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
  public Integer getWellSampleIndex(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getWellSampleIndex(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSamplePosX(int, int, int) */
  public Float getWellSamplePosX(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getWellSamplePosX(plateIndex, wellIndex, wellSampleIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getWellSamplePosY(int, int, int) */
  public Float getWellSamplePosY(int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getWellSamplePosY(plateIndex, wellIndex, wellSampleIndex);
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

  // - WellSampleRef property retrieval -

  /* @see MetadataRetrieve#getWellSampleRefID(int, int, int) */
  public String getWellSampleRefID(int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getWellSampleRefID(screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
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

  /* @see MetadataStore#setArcType(String, int, int) */
  public void setArcType(String type, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcType(type, instrumentIndex, lightSourceIndex);
      }
    }
  }

  // - ChannelComponent property storage -

  /* @see MetadataStore#setChannelComponentColorDomain(String, int, int, int) */
  public void setChannelComponentColorDomain(String colorDomain, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelComponentColorDomain(colorDomain, imageIndex, logicalChannelIndex, channelComponentIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelComponentIndex(Integer, int, int, int) */
  public void setChannelComponentIndex(Integer index, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelComponentIndex(index, imageIndex, logicalChannelIndex, channelComponentIndex);
      }
    }
  }

  /* @see MetadataStore#setChannelComponentPixels(String, int, int, int) */
  public void setChannelComponentPixels(String pixels, int imageIndex, int logicalChannelIndex, int channelComponentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setChannelComponentPixels(pixels, imageIndex, logicalChannelIndex, channelComponentIndex);
      }
    }
  }

  // - Circle property storage -

  /* @see MetadataStore#setCircleCx(String, int, int, int) */
  public void setCircleCx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setCircleCx(cx, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setCircleCy(String, int, int, int) */
  public void setCircleCy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setCircleCy(cy, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setCircleID(String, int, int, int) */
  public void setCircleID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setCircleID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setCircleR(String, int, int, int) */
  public void setCircleR(String r, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setCircleR(r, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setCircleTransform(String, int, int, int) */
  public void setCircleTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setCircleTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - Contact property storage -

  /* @see MetadataStore#setContactExperimenter(String, int) */
  public void setContactExperimenter(String experimenter, int groupIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setContactExperimenter(experimenter, groupIndex);
      }
    }
  }

  // - Dataset property storage -

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

  /* @see MetadataStore#setDatasetLocked(Boolean, int) */
  public void setDatasetLocked(Boolean locked, int datasetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetLocked(locked, datasetIndex);
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

  // - DatasetRef property storage -

  /* @see MetadataStore#setDatasetRefID(String, int, int) */
  public void setDatasetRefID(String id, int imageIndex, int datasetRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDatasetRefID(id, imageIndex, datasetRefIndex);
      }
    }
  }

  // - Detector property storage -

  /* @see MetadataStore#setDetectorAmplificationGain(Float, int, int) */
  public void setDetectorAmplificationGain(Float amplificationGain, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorAmplificationGain(amplificationGain, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorGain(Float, int, int) */
  public void setDetectorGain(Float gain, int instrumentIndex, int detectorIndex) {
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

  /* @see MetadataStore#setDetectorOffset(Float, int, int) */
  public void setDetectorOffset(Float offset, int instrumentIndex, int detectorIndex) {
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

  /* @see MetadataStore#setDetectorType(String, int, int) */
  public void setDetectorType(String type, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorType(type, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorVoltage(Float, int, int) */
  public void setDetectorVoltage(Float voltage, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorVoltage(voltage, instrumentIndex, detectorIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorZoom(Float, int, int) */
  public void setDetectorZoom(Float zoom, int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorZoom(zoom, instrumentIndex, detectorIndex);
      }
    }
  }

  // - DetectorSettings property storage -

  /* @see MetadataStore#setDetectorSettingsBinning(String, int, int) */
  public void setDetectorSettingsBinning(String binning, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsBinning(binning, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsDetector(String, int, int) */
  public void setDetectorSettingsDetector(String detector, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsDetector(detector, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsGain(Float, int, int) */
  public void setDetectorSettingsGain(Float gain, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsGain(gain, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsOffset(Float, int, int) */
  public void setDetectorSettingsOffset(Float offset, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsOffset(offset, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsReadOutRate(Float, int, int) */
  public void setDetectorSettingsReadOutRate(Float readOutRate, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsReadOutRate(readOutRate, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setDetectorSettingsVoltage(Float, int, int) */
  public void setDetectorSettingsVoltage(Float voltage, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDetectorSettingsVoltage(voltage, imageIndex, logicalChannelIndex);
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

  // - Dimensions property storage -

  /* @see MetadataStore#setDimensionsPhysicalSizeX(Float, int, int) */
  public void setDimensionsPhysicalSizeX(Float physicalSizeX, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDimensionsPhysicalSizeX(physicalSizeX, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeY(Float, int, int) */
  public void setDimensionsPhysicalSizeY(Float physicalSizeY, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDimensionsPhysicalSizeY(physicalSizeY, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setDimensionsPhysicalSizeZ(Float, int, int) */
  public void setDimensionsPhysicalSizeZ(Float physicalSizeZ, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDimensionsPhysicalSizeZ(physicalSizeZ, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setDimensionsTimeIncrement(Float, int, int) */
  public void setDimensionsTimeIncrement(Float timeIncrement, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDimensionsTimeIncrement(timeIncrement, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setDimensionsWaveIncrement(Integer, int, int) */
  public void setDimensionsWaveIncrement(Integer waveIncrement, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDimensionsWaveIncrement(waveIncrement, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setDimensionsWaveStart(Integer, int, int) */
  public void setDimensionsWaveStart(Integer waveStart, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDimensionsWaveStart(waveStart, imageIndex, pixelsIndex);
      }
    }
  }

  // - DisplayOptions property storage -

  /* @see MetadataStore#setDisplayOptionsDisplay(String, int) */
  public void setDisplayOptionsDisplay(String display, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDisplayOptionsDisplay(display, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setDisplayOptionsID(String, int) */
  public void setDisplayOptionsID(String id, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDisplayOptionsID(id, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setDisplayOptionsZoom(Float, int) */
  public void setDisplayOptionsZoom(Float zoom, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setDisplayOptionsZoom(zoom, imageIndex);
      }
    }
  }

  // - Ellipse property storage -

  /* @see MetadataStore#setEllipseCx(String, int, int, int) */
  public void setEllipseCx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseCx(cx, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseCy(String, int, int, int) */
  public void setEllipseCy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseCy(cy, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseID(String, int, int, int) */
  public void setEllipseID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseRx(String, int, int, int) */
  public void setEllipseRx(String rx, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseRx(rx, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseRy(String, int, int, int) */
  public void setEllipseRy(String ry, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseRy(ry, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setEllipseTransform(String, int, int, int) */
  public void setEllipseTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEllipseTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - EmFilter property storage -

  /* @see MetadataStore#setEmFilterLotNumber(String, int, int) */
  public void setEmFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEmFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setEmFilterManufacturer(String, int, int) */
  public void setEmFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEmFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setEmFilterModel(String, int, int) */
  public void setEmFilterModel(String model, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEmFilterModel(model, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setEmFilterType(String, int, int) */
  public void setEmFilterType(String type, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setEmFilterType(type, instrumentIndex, filterIndex);
      }
    }
  }

  // - ExFilter property storage -

  /* @see MetadataStore#setExFilterLotNumber(String, int, int) */
  public void setExFilterLotNumber(String lotNumber, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExFilterLotNumber(lotNumber, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setExFilterManufacturer(String, int, int) */
  public void setExFilterManufacturer(String manufacturer, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExFilterManufacturer(manufacturer, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setExFilterModel(String, int, int) */
  public void setExFilterModel(String model, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExFilterModel(model, instrumentIndex, filterIndex);
      }
    }
  }

  /* @see MetadataStore#setExFilterType(String, int, int) */
  public void setExFilterType(String type, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExFilterType(type, instrumentIndex, filterIndex);
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

  /* @see MetadataStore#setExperimentType(String, int) */
  public void setExperimentType(String type, int experimentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimentType(type, experimentIndex);
      }
    }
  }

  // - Experimenter property storage -

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

  /* @see MetadataStore#setExperimenterOMEName(String, int) */
  public void setExperimenterOMEName(String omeName, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterOMEName(omeName, experimenterIndex);
      }
    }
  }

  // - ExperimenterMembership property storage -

  /* @see MetadataStore#setExperimenterMembershipGroup(String, int, int) */
  public void setExperimenterMembershipGroup(String group, int experimenterIndex, int groupRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterMembershipGroup(group, experimenterIndex, groupRefIndex);
      }
    }
  }

  // - Filament property storage -

  /* @see MetadataStore#setFilamentType(String, int, int) */
  public void setFilamentType(String type, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentType(type, instrumentIndex, lightSourceIndex);
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

  /* @see MetadataStore#setFilterType(String, int, int) */
  public void setFilterType(String type, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterType(type, instrumentIndex, filterIndex);
      }
    }
  }

  // - FilterSet property storage -

  /* @see MetadataStore#setFilterSetDichroic(String, int, int) */
  public void setFilterSetDichroic(String dichroic, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetDichroic(dichroic, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetEmFilter(String, int, int) */
  public void setFilterSetEmFilter(String emFilter, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetEmFilter(emFilter, instrumentIndex, filterSetIndex);
      }
    }
  }

  /* @see MetadataStore#setFilterSetExFilter(String, int, int) */
  public void setFilterSetExFilter(String exFilter, int instrumentIndex, int filterSetIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilterSetExFilter(exFilter, instrumentIndex, filterSetIndex);
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

  // - Group property storage -

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

  // - GroupRef property storage -

  // - Image property storage -

  /* @see MetadataStore#setImageAcquiredPixels(String, int) */
  public void setImageAcquiredPixels(String acquiredPixels, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageAcquiredPixels(acquiredPixels, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageCreationDate(String, int) */
  public void setImageCreationDate(String creationDate, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageCreationDate(creationDate, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImageDefaultPixels(String, int) */
  public void setImageDefaultPixels(String defaultPixels, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImageDefaultPixels(defaultPixels, imageIndex);
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

  // - ImagingEnvironment property storage -

  /* @see MetadataStore#setImagingEnvironmentAirPressure(Float, int) */
  public void setImagingEnvironmentAirPressure(Float airPressure, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentAirPressure(airPressure, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImagingEnvironmentCO2Percent(Float, int) */
  public void setImagingEnvironmentCO2Percent(Float cO2Percent, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentCO2Percent(cO2Percent, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImagingEnvironmentHumidity(Float, int) */
  public void setImagingEnvironmentHumidity(Float humidity, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setImagingEnvironmentHumidity(humidity, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setImagingEnvironmentTemperature(Float, int) */
  public void setImagingEnvironmentTemperature(Float temperature, int imageIndex) {
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

  /* @see MetadataStore#setLaserFrequencyMultiplication(Integer, int, int) */
  public void setLaserFrequencyMultiplication(Integer frequencyMultiplication, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserFrequencyMultiplication(frequencyMultiplication, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserLaserMedium(String, int, int) */
  public void setLaserLaserMedium(String laserMedium, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserLaserMedium(laserMedium, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserPockelCell(Boolean, int, int) */
  public void setLaserPockelCell(Boolean pockelCell, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPockelCell(pockelCell, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserPulse(String, int, int) */
  public void setLaserPulse(String pulse, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPulse(pulse, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserRepetitionRate(Boolean, int, int) */
  public void setLaserRepetitionRate(Boolean repetitionRate, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserRepetitionRate(repetitionRate, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserTuneable(Boolean, int, int) */
  public void setLaserTuneable(Boolean tuneable, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserTuneable(tuneable, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserType(String, int, int) */
  public void setLaserType(String type, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserType(type, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLaserWavelength(Integer, int, int) */
  public void setLaserWavelength(Integer wavelength, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserWavelength(wavelength, instrumentIndex, lightSourceIndex);
      }
    }
  }

  // - LightSource property storage -

  /* @see MetadataStore#setLightSourceID(String, int, int) */
  public void setLightSourceID(String id, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceID(id, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceManufacturer(String, int, int) */
  public void setLightSourceManufacturer(String manufacturer, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceManufacturer(manufacturer, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceModel(String, int, int) */
  public void setLightSourceModel(String model, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceModel(model, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourcePower(Float, int, int) */
  public void setLightSourcePower(Float power, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourcePower(power, instrumentIndex, lightSourceIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceSerialNumber(String, int, int) */
  public void setLightSourceSerialNumber(String serialNumber, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceSerialNumber(serialNumber, instrumentIndex, lightSourceIndex);
      }
    }
  }

  // - LightSourceRef property storage -

  /* @see MetadataStore#setLightSourceRefAttenuation(Float, int, int, int) */
  public void setLightSourceRefAttenuation(Float attenuation, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceRefAttenuation(attenuation, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceRefLightSource(String, int, int, int) */
  public void setLightSourceRefLightSource(String lightSource, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceRefLightSource(lightSource, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceRefWavelength(Integer, int, int, int) */
  public void setLightSourceRefWavelength(Integer wavelength, int imageIndex, int microbeamManipulationIndex, int lightSourceRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceRefWavelength(wavelength, imageIndex, microbeamManipulationIndex, lightSourceRefIndex);
      }
    }
  }

  // - LightSourceSettings property storage -

  /* @see MetadataStore#setLightSourceSettingsAttenuation(Float, int, int) */
  public void setLightSourceSettingsAttenuation(Float attenuation, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceSettingsAttenuation(attenuation, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceSettingsLightSource(String, int, int) */
  public void setLightSourceSettingsLightSource(String lightSource, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceSettingsLightSource(lightSource, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLightSourceSettingsWavelength(Integer, int, int) */
  public void setLightSourceSettingsWavelength(Integer wavelength, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLightSourceSettingsWavelength(wavelength, imageIndex, logicalChannelIndex);
      }
    }
  }

  // - Line property storage -

  /* @see MetadataStore#setLineID(String, int, int, int) */
  public void setLineID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineTransform(String, int, int, int) */
  public void setLineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineX1(String, int, int, int) */
  public void setLineX1(String x1, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineX1(x1, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineX2(String, int, int, int) */
  public void setLineX2(String x2, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineX2(x2, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineY1(String, int, int, int) */
  public void setLineY1(String y1, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineY1(y1, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setLineY2(String, int, int, int) */
  public void setLineY2(String y2, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLineY2(y2, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - LogicalChannel property storage -

  /* @see MetadataStore#setLogicalChannelContrastMethod(String, int, int) */
  public void setLogicalChannelContrastMethod(String contrastMethod, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelContrastMethod(contrastMethod, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelDetector(String, int, int) */
  public void setLogicalChannelDetector(String detector, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelDetector(detector, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelEmWave(Integer, int, int) */
  public void setLogicalChannelEmWave(Integer emWave, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelEmWave(emWave, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelExWave(Integer, int, int) */
  public void setLogicalChannelExWave(Integer exWave, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelExWave(exWave, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelFilterSet(String, int, int) */
  public void setLogicalChannelFilterSet(String filterSet, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelFilterSet(filterSet, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelFluor(String, int, int) */
  public void setLogicalChannelFluor(String fluor, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelFluor(fluor, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelID(String, int, int) */
  public void setLogicalChannelID(String id, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelID(id, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelIlluminationType(String, int, int) */
  public void setLogicalChannelIlluminationType(String illuminationType, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelIlluminationType(illuminationType, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelLightSource(String, int, int) */
  public void setLogicalChannelLightSource(String lightSource, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelLightSource(lightSource, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelMode(String, int, int) */
  public void setLogicalChannelMode(String mode, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelMode(mode, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelName(String, int, int) */
  public void setLogicalChannelName(String name, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelName(name, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelNdFilter(Float, int, int) */
  public void setLogicalChannelNdFilter(Float ndFilter, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelNdFilter(ndFilter, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelOTF(String, int, int) */
  public void setLogicalChannelOTF(String otf, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelOTF(otf, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelPhotometricInterpretation(String, int, int) */
  public void setLogicalChannelPhotometricInterpretation(String photometricInterpretation, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelPhotometricInterpretation(photometricInterpretation, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelPinholeSize(Float, int, int) */
  public void setLogicalChannelPinholeSize(Float pinholeSize, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelPockelCellSetting(Integer, int, int) */
  public void setLogicalChannelPockelCellSetting(Integer pockelCellSetting, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelPockelCellSetting(pockelCellSetting, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelSamplesPerPixel(Integer, int, int) */
  public void setLogicalChannelSamplesPerPixel(Integer samplesPerPixel, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelSamplesPerPixel(samplesPerPixel, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelSecondaryEmissionFilter(String, int, int) */
  public void setLogicalChannelSecondaryEmissionFilter(String secondaryEmissionFilter, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelSecondaryEmissionFilter(secondaryEmissionFilter, imageIndex, logicalChannelIndex);
      }
    }
  }

  /* @see MetadataStore#setLogicalChannelSecondaryExcitationFilter(String, int, int) */
  public void setLogicalChannelSecondaryExcitationFilter(String secondaryExcitationFilter, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelSecondaryExcitationFilter(secondaryExcitationFilter, imageIndex, logicalChannelIndex);
      }
    }
  }

  // - Mask property storage -

  /* @see MetadataStore#setMaskHeight(String, int, int, int) */
  public void setMaskHeight(String height, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskHeight(height, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskID(String, int, int, int) */
  public void setMaskID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskTransform(String, int, int, int) */
  public void setMaskTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskWidth(String, int, int, int) */
  public void setMaskWidth(String width, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskWidth(width, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskX(String, int, int, int) */
  public void setMaskX(String x, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskX(x, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskY(String, int, int, int) */
  public void setMaskY(String y, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskY(y, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - MaskPixels property storage -

  /* @see MetadataStore#setMaskPixelsBigEndian(Boolean, int, int, int) */
  public void setMaskPixelsBigEndian(Boolean bigEndian, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskPixelsBigEndian(bigEndian, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskPixelsBinData(byte[], int, int, int) */
  public void setMaskPixelsBinData(byte[] binData, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskPixelsBinData(binData, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskPixelsExtendedPixelType(String, int, int, int) */
  public void setMaskPixelsExtendedPixelType(String extendedPixelType, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskPixelsExtendedPixelType(extendedPixelType, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskPixelsID(String, int, int, int) */
  public void setMaskPixelsID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskPixelsID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskPixelsSizeX(Integer, int, int, int) */
  public void setMaskPixelsSizeX(Integer sizeX, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskPixelsSizeX(sizeX, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setMaskPixelsSizeY(Integer, int, int, int) */
  public void setMaskPixelsSizeY(Integer sizeY, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMaskPixelsSizeY(sizeY, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - MicrobeamManipulation property storage -

  /* @see MetadataStore#setMicrobeamManipulationExperimenterRef(String, int, int) */
  public void setMicrobeamManipulationExperimenterRef(String experimenterRef, int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationExperimenterRef(experimenterRef, imageIndex, microbeamManipulationIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationID(String, int, int) */
  public void setMicrobeamManipulationID(String id, int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationID(id, imageIndex, microbeamManipulationIndex);
      }
    }
  }

  /* @see MetadataStore#setMicrobeamManipulationType(String, int, int) */
  public void setMicrobeamManipulationType(String type, int imageIndex, int microbeamManipulationIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationType(type, imageIndex, microbeamManipulationIndex);
      }
    }
  }

  // - MicrobeamManipulationRef property storage -

  /* @see MetadataStore#setMicrobeamManipulationRefID(String, int, int) */
  public void setMicrobeamManipulationRefID(String id, int experimentIndex, int microbeamManipulationRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicrobeamManipulationRefID(id, experimentIndex, microbeamManipulationRefIndex);
      }
    }
  }

  // - Microscope property storage -

  /* @see MetadataStore#setMicroscopeID(String, int) */
  public void setMicroscopeID(String id, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeID(id, instrumentIndex);
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

  /* @see MetadataStore#setMicroscopeType(String, int) */
  public void setMicroscopeType(String type, int instrumentIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setMicroscopeType(type, instrumentIndex);
      }
    }
  }

  // - OTF property storage -

  /* @see MetadataStore#setOTFBinaryFile(String, int, int) */
  public void setOTFBinaryFile(String binaryFile, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFBinaryFile(binaryFile, instrumentIndex, otfIndex);
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

  /* @see MetadataStore#setOTFObjective(String, int, int) */
  public void setOTFObjective(String objective, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFObjective(objective, instrumentIndex, otfIndex);
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

  /* @see MetadataStore#setOTFPixelType(String, int, int) */
  public void setOTFPixelType(String pixelType, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFPixelType(pixelType, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFSizeX(Integer, int, int) */
  public void setOTFSizeX(Integer sizeX, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFSizeX(sizeX, instrumentIndex, otfIndex);
      }
    }
  }

  /* @see MetadataStore#setOTFSizeY(Integer, int, int) */
  public void setOTFSizeY(Integer sizeY, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFSizeY(sizeY, instrumentIndex, otfIndex);
      }
    }
  }

  // - Objective property storage -

  /* @see MetadataStore#setObjectiveCalibratedMagnification(Float, int, int) */
  public void setObjectiveCalibratedMagnification(Float calibratedMagnification, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveCalibratedMagnification(calibratedMagnification, instrumentIndex, objectiveIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveCorrection(String, int, int) */
  public void setObjectiveCorrection(String correction, int instrumentIndex, int objectiveIndex) {
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

  /* @see MetadataStore#setObjectiveImmersion(String, int, int) */
  public void setObjectiveImmersion(String immersion, int instrumentIndex, int objectiveIndex) {
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

  /* @see MetadataStore#setObjectiveLensNA(Float, int, int) */
  public void setObjectiveLensNA(Float lensNA, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveLensNA(lensNA, instrumentIndex, objectiveIndex);
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

  /* @see MetadataStore#setObjectiveWorkingDistance(Float, int, int) */
  public void setObjectiveWorkingDistance(Float workingDistance, int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveWorkingDistance(workingDistance, instrumentIndex, objectiveIndex);
      }
    }
  }

  // - ObjectiveSettings property storage -

  /* @see MetadataStore#setObjectiveSettingsCorrectionCollar(Float, int) */
  public void setObjectiveSettingsCorrectionCollar(Float correctionCollar, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveSettingsCorrectionCollar(correctionCollar, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveSettingsMedium(String, int) */
  public void setObjectiveSettingsMedium(String medium, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveSettingsMedium(medium, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveSettingsObjective(String, int) */
  public void setObjectiveSettingsObjective(String objective, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveSettingsObjective(objective, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setObjectiveSettingsRefractiveIndex(Float, int) */
  public void setObjectiveSettingsRefractiveIndex(Float refractiveIndex, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setObjectiveSettingsRefractiveIndex(refractiveIndex, imageIndex);
      }
    }
  }

  // - Path property storage -

  /* @see MetadataStore#setPathD(String, int, int, int) */
  public void setPathD(String d, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathD(d, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPathID(String, int, int, int) */
  public void setPathID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPathID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - Pixels property storage -

  /* @see MetadataStore#setPixelsBigEndian(Boolean, int, int) */
  public void setPixelsBigEndian(Boolean bigEndian, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsBigEndian(bigEndian, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsDimensionOrder(String, int, int) */
  public void setPixelsDimensionOrder(String dimensionOrder, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsDimensionOrder(dimensionOrder, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsID(String, int, int) */
  public void setPixelsID(String id, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsID(id, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsPixelType(String, int, int) */
  public void setPixelsPixelType(String pixelType, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsPixelType(pixelType, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeC(Integer, int, int) */
  public void setPixelsSizeC(Integer sizeC, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeC(sizeC, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeT(Integer, int, int) */
  public void setPixelsSizeT(Integer sizeT, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeT(sizeT, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeX(Integer, int, int) */
  public void setPixelsSizeX(Integer sizeX, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeX(sizeX, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeY(Integer, int, int) */
  public void setPixelsSizeY(Integer sizeY, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeY(sizeY, imageIndex, pixelsIndex);
      }
    }
  }

  /* @see MetadataStore#setPixelsSizeZ(Integer, int, int) */
  public void setPixelsSizeZ(Integer sizeZ, int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPixelsSizeZ(sizeZ, imageIndex, pixelsIndex);
      }
    }
  }

  // - Plane property storage -

  /* @see MetadataStore#setPlaneHashSHA1(String, int, int, int) */
  public void setPlaneHashSHA1(String hashSHA1, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneHashSHA1(hashSHA1, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneID(String, int, int, int) */
  public void setPlaneID(String id, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneID(id, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTheC(Integer, int, int, int) */
  public void setPlaneTheC(Integer theC, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTheC(theC, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTheT(Integer, int, int, int) */
  public void setPlaneTheT(Integer theT, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTheT(theT, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTheZ(Integer, int, int, int) */
  public void setPlaneTheZ(Integer theZ, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTheZ(theZ, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  // - PlaneTiming property storage -

  /* @see MetadataStore#setPlaneTimingDeltaT(Float, int, int, int) */
  public void setPlaneTimingDeltaT(Float deltaT, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTimingDeltaT(deltaT, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setPlaneTimingExposureTime(Float, int, int, int) */
  public void setPlaneTimingExposureTime(Float exposureTime, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlaneTimingExposureTime(exposureTime, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  // - Plate property storage -

  /* @see MetadataStore#setPlateColumnNamingConvention(String, int) */
  public void setPlateColumnNamingConvention(String columnNamingConvention, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateColumnNamingConvention(columnNamingConvention, plateIndex);
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

  /* @see MetadataStore#setPlateRowNamingConvention(String, int) */
  public void setPlateRowNamingConvention(String rowNamingConvention, int plateIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateRowNamingConvention(rowNamingConvention, plateIndex);
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

  // - PlateRef property storage -

  /* @see MetadataStore#setPlateRefID(String, int, int) */
  public void setPlateRefID(String id, int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateRefID(id, screenIndex, plateRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateRefSample(Integer, int, int) */
  public void setPlateRefSample(Integer sample, int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateRefSample(sample, screenIndex, plateRefIndex);
      }
    }
  }

  /* @see MetadataStore#setPlateRefWell(String, int, int) */
  public void setPlateRefWell(String well, int screenIndex, int plateRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPlateRefWell(well, screenIndex, plateRefIndex);
      }
    }
  }

  // - Point property storage -

  /* @see MetadataStore#setPointCx(String, int, int, int) */
  public void setPointCx(String cx, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointCx(cx, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointCy(String, int, int, int) */
  public void setPointCy(String cy, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointCy(cy, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointID(String, int, int, int) */
  public void setPointID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointR(String, int, int, int) */
  public void setPointR(String r, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointR(r, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPointTransform(String, int, int, int) */
  public void setPointTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPointTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - Polygon property storage -

  /* @see MetadataStore#setPolygonID(String, int, int, int) */
  public void setPolygonID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolygonID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolygonPoints(String, int, int, int) */
  public void setPolygonPoints(String points, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolygonPoints(points, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolygonTransform(String, int, int, int) */
  public void setPolygonTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolygonTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - Polyline property storage -

  /* @see MetadataStore#setPolylineID(String, int, int, int) */
  public void setPolylineID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylinePoints(String, int, int, int) */
  public void setPolylinePoints(String points, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylinePoints(points, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setPolylineTransform(String, int, int, int) */
  public void setPolylineTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPolylineTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - Project property storage -

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

  // - ProjectRef property storage -

  /* @see MetadataStore#setProjectRefID(String, int, int) */
  public void setProjectRefID(String id, int datasetIndex, int projectRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setProjectRefID(id, datasetIndex, projectRefIndex);
      }
    }
  }

  // - Pump property storage -

  /* @see MetadataStore#setPumpLightSource(String, int, int) */
  public void setPumpLightSource(String lightSource, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setPumpLightSource(lightSource, instrumentIndex, lightSourceIndex);
      }
    }
  }

  // - ROI property storage -

  /* @see MetadataStore#setROIID(String, int, int) */
  public void setROIID(String id, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIID(id, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIT0(Integer, int, int) */
  public void setROIT0(Integer t0, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIT0(t0, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIT1(Integer, int, int) */
  public void setROIT1(Integer t1, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIT1(t1, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIX0(Integer, int, int) */
  public void setROIX0(Integer x0, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIX0(x0, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIX1(Integer, int, int) */
  public void setROIX1(Integer x1, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIX1(x1, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIY0(Integer, int, int) */
  public void setROIY0(Integer y0, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIY0(y0, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIY1(Integer, int, int) */
  public void setROIY1(Integer y1, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIY1(y1, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIZ0(Integer, int, int) */
  public void setROIZ0(Integer z0, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIZ0(z0, imageIndex, roiIndex);
      }
    }
  }

  /* @see MetadataStore#setROIZ1(Integer, int, int) */
  public void setROIZ1(Integer z1, int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIZ1(z1, imageIndex, roiIndex);
      }
    }
  }

  // - ROIRef property storage -

  /* @see MetadataStore#setROIRefID(String, int, int, int) */
  public void setROIRefID(String id, int imageIndex, int microbeamManipulationIndex, int roiRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setROIRefID(id, imageIndex, microbeamManipulationIndex, roiRefIndex);
      }
    }
  }

  // - Reagent property storage -

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

  // - Rect property storage -

  /* @see MetadataStore#setRectHeight(String, int, int, int) */
  public void setRectHeight(String height, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectHeight(height, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectID(String, int, int, int) */
  public void setRectID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectTransform(String, int, int, int) */
  public void setRectTransform(String transform, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectTransform(transform, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectWidth(String, int, int, int) */
  public void setRectWidth(String width, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectWidth(width, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectX(String, int, int, int) */
  public void setRectX(String x, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectX(x, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setRectY(String, int, int, int) */
  public void setRectY(String y, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRectY(y, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  // - Region property storage -

  /* @see MetadataStore#setRegionID(String, int, int) */
  public void setRegionID(String id, int imageIndex, int regionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRegionID(id, imageIndex, regionIndex);
      }
    }
  }

  /* @see MetadataStore#setRegionName(String, int, int) */
  public void setRegionName(String name, int imageIndex, int regionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRegionName(name, imageIndex, regionIndex);
      }
    }
  }

  /* @see MetadataStore#setRegionTag(String, int, int) */
  public void setRegionTag(String tag, int imageIndex, int regionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRegionTag(tag, imageIndex, regionIndex);
      }
    }
  }

  // - RoiLink property storage -

  /* @see MetadataStore#setRoiLinkDirection(String, int, int, int) */
  public void setRoiLinkDirection(String direction, int imageIndex, int roiIndex, int roiLinkIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRoiLinkDirection(direction, imageIndex, roiIndex, roiLinkIndex);
      }
    }
  }

  /* @see MetadataStore#setRoiLinkName(String, int, int, int) */
  public void setRoiLinkName(String name, int imageIndex, int roiIndex, int roiLinkIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRoiLinkName(name, imageIndex, roiIndex, roiLinkIndex);
      }
    }
  }

  /* @see MetadataStore#setRoiLinkRef(String, int, int, int) */
  public void setRoiLinkRef(String ref, int imageIndex, int roiIndex, int roiLinkIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setRoiLinkRef(ref, imageIndex, roiIndex, roiLinkIndex);
      }
    }
  }

  // - Screen property storage -

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

  /* @see MetadataStore#setScreenExtern(String, int) */
  public void setScreenExtern(String extern, int screenIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenExtern(extern, screenIndex);
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

  // - ScreenAcquisition property storage -

  /* @see MetadataStore#setScreenAcquisitionEndTime(String, int, int) */
  public void setScreenAcquisitionEndTime(String endTime, int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenAcquisitionEndTime(endTime, screenIndex, screenAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenAcquisitionID(String, int, int) */
  public void setScreenAcquisitionID(String id, int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenAcquisitionID(id, screenIndex, screenAcquisitionIndex);
      }
    }
  }

  /* @see MetadataStore#setScreenAcquisitionStartTime(String, int, int) */
  public void setScreenAcquisitionStartTime(String startTime, int screenIndex, int screenAcquisitionIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenAcquisitionStartTime(startTime, screenIndex, screenAcquisitionIndex);
      }
    }
  }

  // - ScreenRef property storage -

  /* @see MetadataStore#setScreenRefID(String, int, int) */
  public void setScreenRefID(String id, int plateIndex, int screenRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setScreenRefID(id, plateIndex, screenRefIndex);
      }
    }
  }

  // - Shape property storage -

  /* @see MetadataStore#setShapeBaselineShift(String, int, int, int) */
  public void setShapeBaselineShift(String baselineShift, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeBaselineShift(baselineShift, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeDirection(String, int, int, int) */
  public void setShapeDirection(String direction, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeDirection(direction, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFillColor(String, int, int, int) */
  public void setShapeFillColor(String fillColor, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFillColor(fillColor, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFillOpacity(String, int, int, int) */
  public void setShapeFillOpacity(String fillOpacity, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFillOpacity(fillOpacity, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFillRule(String, int, int, int) */
  public void setShapeFillRule(String fillRule, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFillRule(fillRule, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFontFamily(String, int, int, int) */
  public void setShapeFontFamily(String fontFamily, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFontFamily(fontFamily, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFontSize(Integer, int, int, int) */
  public void setShapeFontSize(Integer fontSize, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFontSize(fontSize, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFontStretch(String, int, int, int) */
  public void setShapeFontStretch(String fontStretch, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFontStretch(fontStretch, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFontStyle(String, int, int, int) */
  public void setShapeFontStyle(String fontStyle, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFontStyle(fontStyle, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFontVariant(String, int, int, int) */
  public void setShapeFontVariant(String fontVariant, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFontVariant(fontVariant, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeFontWeight(String, int, int, int) */
  public void setShapeFontWeight(String fontWeight, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeFontWeight(fontWeight, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeG(String, int, int, int) */
  public void setShapeG(String g, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeG(g, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeGlyphOrientationVertical(Integer, int, int, int) */
  public void setShapeGlyphOrientationVertical(Integer glyphOrientationVertical, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeGlyphOrientationVertical(glyphOrientationVertical, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeID(String, int, int, int) */
  public void setShapeID(String id, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeID(id, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeLocked(Boolean, int, int, int) */
  public void setShapeLocked(Boolean locked, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeLocked(locked, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeAttribute(String, int, int, int) */
  public void setShapeStrokeAttribute(String strokeAttribute, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeAttribute(strokeAttribute, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeColor(String, int, int, int) */
  public void setShapeStrokeColor(String strokeColor, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeColor(strokeColor, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeDashArray(String, int, int, int) */
  public void setShapeStrokeDashArray(String strokeDashArray, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeDashArray(strokeDashArray, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeLineCap(String, int, int, int) */
  public void setShapeStrokeLineCap(String strokeLineCap, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeLineCap(strokeLineCap, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeLineJoin(String, int, int, int) */
  public void setShapeStrokeLineJoin(String strokeLineJoin, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeLineJoin(strokeLineJoin, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeMiterLimit(Integer, int, int, int) */
  public void setShapeStrokeMiterLimit(Integer strokeMiterLimit, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeMiterLimit(strokeMiterLimit, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeOpacity(Float, int, int, int) */
  public void setShapeStrokeOpacity(Float strokeOpacity, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeOpacity(strokeOpacity, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeStrokeWidth(Integer, int, int, int) */
  public void setShapeStrokeWidth(Integer strokeWidth, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeStrokeWidth(strokeWidth, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeText(String, int, int, int) */
  public void setShapeText(String text, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeText(text, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeTextAnchor(String, int, int, int) */
  public void setShapeTextAnchor(String textAnchor, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeTextAnchor(textAnchor, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeTextDecoration(String, int, int, int) */
  public void setShapeTextDecoration(String textDecoration, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeTextDecoration(textDecoration, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeTextFill(String, int, int, int) */
  public void setShapeTextFill(String textFill, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeTextFill(textFill, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeTextStroke(String, int, int, int) */
  public void setShapeTextStroke(String textStroke, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeTextStroke(textStroke, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeTheT(Integer, int, int, int) */
  public void setShapeTheT(Integer theT, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeTheT(theT, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeTheZ(Integer, int, int, int) */
  public void setShapeTheZ(Integer theZ, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeTheZ(theZ, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeVectorEffect(String, int, int, int) */
  public void setShapeVectorEffect(String vectorEffect, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeVectorEffect(vectorEffect, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeVisibility(Boolean, int, int, int) */
  public void setShapeVisibility(Boolean visibility, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeVisibility(visibility, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

  /* @see MetadataStore#setShapeWritingMode(String, int, int, int) */
  public void setShapeWritingMode(String writingMode, int imageIndex, int roiIndex, int shapeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setShapeWritingMode(writingMode, imageIndex, roiIndex, shapeIndex);
      }
    }
  }

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

  /* @see MetadataStore#setStageLabelX(Float, int) */
  public void setStageLabelX(Float x, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelX(x, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setStageLabelY(Float, int) */
  public void setStageLabelY(Float y, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelY(y, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setStageLabelZ(Float, int) */
  public void setStageLabelZ(Float z, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStageLabelZ(z, imageIndex);
      }
    }
  }

  // - StagePosition property storage -

  /* @see MetadataStore#setStagePositionPositionX(Float, int, int, int) */
  public void setStagePositionPositionX(Float positionX, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStagePositionPositionX(positionX, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setStagePositionPositionY(Float, int, int, int) */
  public void setStagePositionPositionY(Float positionY, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStagePositionPositionY(positionY, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  /* @see MetadataStore#setStagePositionPositionZ(Float, int, int, int) */
  public void setStagePositionPositionZ(Float positionZ, int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setStagePositionPositionZ(positionZ, imageIndex, pixelsIndex, planeIndex);
      }
    }
  }

  // - Thumbnail property storage -

  /* @see MetadataStore#setThumbnailHref(String, int) */
  public void setThumbnailHref(String href, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setThumbnailHref(href, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setThumbnailID(String, int) */
  public void setThumbnailID(String id, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setThumbnailID(id, imageIndex);
      }
    }
  }

  /* @see MetadataStore#setThumbnailMIMEtype(String, int) */
  public void setThumbnailMIMEtype(String mimEtype, int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setThumbnailMIMEtype(mimEtype, imageIndex);
      }
    }
  }

  // - TiffData property storage -

  /* @see MetadataStore#setTiffDataFileName(String, int, int, int) */
  public void setTiffDataFileName(String fileName, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFileName(fileName, imageIndex, pixelsIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataFirstC(Integer, int, int, int) */
  public void setTiffDataFirstC(Integer firstC, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFirstC(firstC, imageIndex, pixelsIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataFirstT(Integer, int, int, int) */
  public void setTiffDataFirstT(Integer firstT, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFirstT(firstT, imageIndex, pixelsIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataFirstZ(Integer, int, int, int) */
  public void setTiffDataFirstZ(Integer firstZ, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataFirstZ(firstZ, imageIndex, pixelsIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataIFD(Integer, int, int, int) */
  public void setTiffDataIFD(Integer ifd, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataIFD(ifd, imageIndex, pixelsIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataNumPlanes(Integer, int, int, int) */
  public void setTiffDataNumPlanes(Integer numPlanes, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataNumPlanes(numPlanes, imageIndex, pixelsIndex, tiffDataIndex);
      }
    }
  }

  /* @see MetadataStore#setTiffDataUUID(String, int, int, int) */
  public void setTiffDataUUID(String uuid, int imageIndex, int pixelsIndex, int tiffDataIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTiffDataUUID(uuid, imageIndex, pixelsIndex, tiffDataIndex);
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

  /* @see MetadataStore#setTransmittanceRangeTransmittance(Integer, int, int) */
  public void setTransmittanceRangeTransmittance(Integer transmittance, int instrumentIndex, int filterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setTransmittanceRangeTransmittance(transmittance, instrumentIndex, filterIndex);
      }
    }
  }

  // - Well property storage -

  /* @see MetadataStore#setWellColumn(Integer, int, int) */
  public void setWellColumn(Integer column, int plateIndex, int wellIndex) {
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

  /* @see MetadataStore#setWellReagent(String, int, int) */
  public void setWellReagent(String reagent, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellReagent(reagent, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellRow(Integer, int, int) */
  public void setWellRow(Integer row, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellRow(row, plateIndex, wellIndex);
      }
    }
  }

  /* @see MetadataStore#setWellType(String, int, int) */
  public void setWellType(String type, int plateIndex, int wellIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellType(type, plateIndex, wellIndex);
      }
    }
  }

  // - WellSample property storage -

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

  /* @see MetadataStore#setWellSampleIndex(Integer, int, int, int) */
  public void setWellSampleIndex(Integer index, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleIndex(index, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSamplePosX(Float, int, int, int) */
  public void setWellSamplePosX(Float posX, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSamplePosX(posX, plateIndex, wellIndex, wellSampleIndex);
      }
    }
  }

  /* @see MetadataStore#setWellSamplePosY(Float, int, int, int) */
  public void setWellSamplePosY(Float posY, int plateIndex, int wellIndex, int wellSampleIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSamplePosY(posY, plateIndex, wellIndex, wellSampleIndex);
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

  // - WellSampleRef property storage -

  /* @see MetadataStore#setWellSampleRefID(String, int, int, int) */
  public void setWellSampleRefID(String id, int screenIndex, int screenAcquisitionIndex, int wellSampleRefIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setWellSampleRefID(id, screenIndex, screenAcquisitionIndex, wellSampleRefIndex);
      }
    }
  }

}
