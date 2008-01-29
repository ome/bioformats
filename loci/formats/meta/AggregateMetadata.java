//
// AggregateMetadata.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

/*-----------------------------------------------------------------------------
 *
 * THIS IS AUTOMATICALLY GENERATED CODE.  DO NOT MODIFY.
 * Created by curtis via MetadataAutogen on Jan 29, 2008 2:12:52 PM CST
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
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/AggregateMetadata.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/AggregateMetadata.java">SVN</a></dd></dl>
 *
 * @author Chris Allan callan at blackcat.ca
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class AggregateMetadata implements MetadataRetrieve, MetadataStore {

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

  // - Entity retrieval -

  /* @see MetadataRetrieve#getImage(int) */
  public Object getImage(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getImage(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPixels(int, int) */
  public Object getPixels(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getPixels(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDimensions(int, int) */
  public Object getDimensions(int imageIndex, int pixelsIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getDimensions(imageIndex, pixelsIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getImagingEnvironment(int) */
  public Object getImagingEnvironment(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getImagingEnvironment(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlane(int, int, int) */
  public Object getPlane(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getPlane(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getPlaneTiming(int, int, int) */
  public Object getPlaneTiming(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getPlaneTiming(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStagePosition(int, int, int) */
  public Object getStagePosition(int imageIndex, int pixelsIndex, int planeIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getStagePosition(imageIndex, pixelsIndex, planeIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLogicalChannel(int, int) */
  public Object getLogicalChannel(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getLogicalChannel(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetectorSettings(int, int) */
  public Object getDetectorSettings(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getDetectorSettings(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSourceSettings(int, int) */
  public Object getLightSourceSettings(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getLightSourceSettings(imageIndex, logicalChannelIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getROI(int, int) */
  public Object getROI(int imageIndex, int roiIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getROI(imageIndex, roiIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getStageLabel(int) */
  public Object getStageLabel(int imageIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getStageLabel(imageIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLightSource(int, int) */
  public Object getLightSource(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getLightSource(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getLaser(int, int) */
  public Object getLaser(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getLaser(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getFilament(int, int) */
  public Object getFilament(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getFilament(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getArc(int, int) */
  public Object getArc(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getArc(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getDetector(int, int) */
  public Object getDetector(int instrumentIndex, int detectorIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getDetector(instrumentIndex, detectorIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getObjective(int, int) */
  public Object getObjective(int instrumentIndex, int objectiveIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getObjective(instrumentIndex, objectiveIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getOTF(int, int) */
  public Object getOTF(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getOTF(instrumentIndex, otfIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  /* @see MetadataRetrieve#getExperimenter(int) */
  public Object getExperimenter(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Object result = retrieve.getExperimenter(experimenterIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Image property retrieval -

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

  // - Pixels property retrieval -

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

  // - ImagingEnvironment property retrieval -

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

  // - Plane property retrieval -

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

  // - LogicalChannel property retrieval -

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

  /* @see MetadataRetrieve#getLogicalChannelPinholeSize(int, int) */
  public Integer getLogicalChannelPinholeSize(int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Integer result = retrieve.getLogicalChannelPinholeSize(imageIndex, logicalChannelIndex);
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

  // - DetectorSettings property retrieval -

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

  // - ROI property retrieval -

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

  // - LightSource property retrieval -

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

  // - Laser property retrieval -

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

  /* @see MetadataRetrieve#getLaserPower(int, int) */
  public Float getLaserPower(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getLaserPower(instrumentIndex, lightSourceIndex);
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

  /* @see MetadataRetrieve#getFilamentPower(int, int) */
  public Float getFilamentPower(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getFilamentPower(instrumentIndex, lightSourceIndex);
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

  /* @see MetadataRetrieve#getArcPower(int, int) */
  public Float getArcPower(int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        Float result = retrieve.getArcPower(instrumentIndex, lightSourceIndex);
        if (result != null) return result;
      }
    }
    return null;
  }

  // - Detector property retrieval -

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

  // - Objective property retrieval -

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

  // - OTF property retrieval -

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

  /* @see MetadataRetrieve#getOTFPath(int, int) */
  public String getOTFPath(int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getOTFPath(instrumentIndex, otfIndex);
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

  // - Experimenter property retrieval -

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

  /* @see MetadataRetrieve#getExperimenterDataDirectory(int) */
  public String getExperimenterDataDirectory(int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataRetrieve) {
        MetadataRetrieve retrieve = (MetadataRetrieve) o;
        String result = retrieve.getExperimenterDataDirectory(experimenterIndex);
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

  // - Image property storage -

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

  // - Pixels property storage -

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

  // - ImagingEnvironment property storage -

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

  // - Plane property storage -

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

  // - LogicalChannel property storage -

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

  /* @see MetadataStore#setLogicalChannelPinholeSize(Integer, int, int) */
  public void setLogicalChannelPinholeSize(Integer pinholeSize, int imageIndex, int logicalChannelIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLogicalChannelPinholeSize(pinholeSize, imageIndex, logicalChannelIndex);
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

  // - DetectorSettings property storage -

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

  // - ROI property storage -

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

  // - LightSource property storage -

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

  // - Laser property storage -

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

  /* @see MetadataStore#setLaserPower(Float, int, int) */
  public void setLaserPower(Float power, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setLaserPower(power, instrumentIndex, lightSourceIndex);
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

  /* @see MetadataStore#setFilamentPower(Float, int, int) */
  public void setFilamentPower(Float power, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setFilamentPower(power, instrumentIndex, lightSourceIndex);
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

  /* @see MetadataStore#setArcPower(Float, int, int) */
  public void setArcPower(Float power, int instrumentIndex, int lightSourceIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setArcPower(power, instrumentIndex, lightSourceIndex);
      }
    }
  }

  // - Detector property storage -

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

  // - Objective property storage -

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

  // - OTF property storage -

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

  /* @see MetadataStore#setOTFPath(String, int, int) */
  public void setOTFPath(String path, int instrumentIndex, int otfIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setOTFPath(path, instrumentIndex, otfIndex);
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

  // - Experimenter property storage -

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

  /* @see MetadataStore#setExperimenterDataDirectory(String, int) */
  public void setExperimenterDataDirectory(String dataDirectory, int experimenterIndex) {
    for (Iterator iter = delegates.iterator(); iter.hasNext();) {
      Object o = iter.next();
      if (o instanceof MetadataStore) {
        MetadataStore store = (MetadataStore) o;
        store.setExperimenterDataDirectory(dataDirectory, experimenterIndex);
      }
    }
  }

}
