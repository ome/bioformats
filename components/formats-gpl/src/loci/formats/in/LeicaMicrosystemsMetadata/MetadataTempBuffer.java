/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package loci.formats.in.LeicaMicrosystemsMetadata;

import java.util.List;

import loci.formats.in.LeicaMicrosystemsMetadata.Dimension.DimensionKey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Arrays;

import ome.units.quantity.Length;
import ome.xml.model.primitives.Color;

/**
 * This class is used to temporarily store metadata information extracted from
 * Leica XML before it is written to the reader's MetadataStore
 * 
 * @author Melissa Linkert melissa at glencoesoftware.com
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class MetadataTempBuffer {
  // -- Fields --
  public List<ArrayList<Color>> channelColors = new ArrayList<ArrayList<Color>>();
  public int[][] channelPrios;

  public List<Double> physicalSizeXs = new ArrayList<Double>();
  public List<Double> physicalSizeYs = new ArrayList<Double>();
  public List<Length> fieldPosX = new ArrayList<Length>();
  public List<Length> fieldPosY = new ArrayList<Length>();

  public String[] descriptions;
  public Double[] pinholes, zSteps, tSteps, lensNA;
  public boolean[] flipX, flipY, swapXY;
  public Double[][] expTimes, gains, detectorOffsets;
  public String[][] channelNames;
  public Double[][] exWaves;
  public ArrayList<ArrayList<Boolean>> activeDetector;

  public String[] corrections;
  public Length[] posX, posY, posZ;
  public Double[] refractiveIndex;
  public ArrayList<ArrayList<Length>> cutIns, cutOuts;
  public ArrayList<ArrayList<String>> filterModels;
  public Double[][] timestamps;
  public ArrayList<ArrayList<Double>> laserIntensity, laserWavelength;
  public ArrayList<ArrayList<Boolean>> laserActive, laserFrap;

  public ROI[][] imageROIs;
  public boolean alternateCenter = false;
  public String[] imageNames;
  public double[] acquiredDate;

  public int[] tileCount;
  public long[] tileBytesInc;
  public boolean[] inverseRgb; // true if channels are in BGR order
  private ArrayList<ArrayList<Dimension>> dimensions;
  public ArrayList<ArrayList<Channel>> channels;

  public ArrayList<ArrayList<Detector>> detectors; // detector info added to an instrument in OME model
  public ArrayList<ArrayList<DetectorSetting>> detectorSettings; // detector info added to detector settings of a
                                                                 // channel in OME model
  public ArrayList<ArrayList<Laser>> lasers;
  public ArrayList<ArrayList<Filter>> filters;

  public enum DataSourceType {
    CAMERA, CONFOCAL
  }

  public DataSourceType[] dataSourceTypes;

  // -- Constructor --
  /**
   * Constructs a MetadataTempBuffer for a given number of images.
   * 
   * @param len
   *          Number of images (one per XLIF)
   */
  public MetadataTempBuffer(int len) {
    tileCount = new int[len];
    Arrays.fill(tileCount, 1);
    tileBytesInc = new long[len];
    acquiredDate = new double[len];
    descriptions = new String[len];
    timestamps = new Double[len][];
    lensNA = new Double[len];
    corrections = new String[len];
    posX = new Length[len];
    posY = new Length[len];
    posZ = new Length[len];
    refractiveIndex = new Double[len];
    zSteps = new Double[len];
    tSteps = new Double[len];
    pinholes = new Double[len];
    flipX = new boolean[len];
    flipY = new boolean[len];
    swapXY = new boolean[len];
    expTimes = new Double[len][];
    gains = new Double[len][];
    detectorOffsets = new Double[len][];
    channelNames = new String[len][];
    exWaves = new Double[len][];
    imageROIs = new ROI[len][];
    imageNames = new String[len];
    inverseRgb = new boolean[len];
    dataSourceTypes = new DataSourceType[len];

    laserWavelength = ArrayListOfArrayLists(len, Double.class);
    activeDetector = ArrayListOfArrayLists(len, Boolean.class);
    cutIns = ArrayListOfArrayLists(len, Length.class);
    cutOuts = ArrayListOfArrayLists(len, Length.class);
    filterModels = ArrayListOfArrayLists(len, String.class);
    laserIntensity = ArrayListOfArrayLists(len, Double.class);
    laserActive = ArrayListOfArrayLists(len, Boolean.class);
    laserFrap = ArrayListOfArrayLists(len, Boolean.class);
    dimensions = ArrayListOfArrayLists(len, Dimension.class);
    channels = ArrayListOfArrayLists(len, Channel.class);
    detectors = ArrayListOfArrayLists(len, Detector.class);
    detectorSettings = ArrayListOfArrayLists(len, DetectorSetting.class);
    lasers = ArrayListOfArrayLists(len, Laser.class);
    filters = ArrayListOfArrayLists(len, Filter.class);
  }

  // -- Methods --
  public Dimension getDimension(int imageIndex, DimensionKey key) {
    for (Dimension dimension : dimensions.get(imageIndex)) {
      if (dimension.key == key)
        return dimension;
    }
    return null;
  }

  /**
   * Inserts dimension to buffer and optionally adapts other dimension-dependent
   * values
   * 
   * @param imageIndex
   * @param dimension
   */
  public void addDimension(int imageIndex, Dimension dimension) {
    dimensions.get(imageIndex).add(dimension);
    if (dimension.key == DimensionKey.X) {
      physicalSizeXs.add(dimension.getLength());
    } else if (dimension.key == DimensionKey.Y) {
      physicalSizeYs.add(dimension.getLength());
    } else if (dimension.key == DimensionKey.Z) {
      if (zSteps[imageIndex] == null && dimension.getLength() != null) {
        zSteps[imageIndex] = Math.abs(dimension.getLength());
      }
    } else if (dimension.key == DimensionKey.S) {
      tileCount[imageIndex] *= dimension.size;
      tileBytesInc[imageIndex] = dimension.bytesInc;
    }
  }

  /**
   * Returns the dimension order as a string
   * 
   * @param imageIndex
   * @return dimension order string, as it is expected in
   *         CoreMetadata.dimensionOrder
   */
  public String getDimensionOrder(int imageIndex) {
    sortDimensions(imageIndex);

    String dimensionOrder = "";
    List<DimensionKey> standardDimensions = new ArrayList<>(
        Arrays.asList(DimensionKey.X, DimensionKey.Y, DimensionKey.Z,
            DimensionKey.C, DimensionKey.T));

    for (Dimension dimension : dimensions.get(imageIndex)) {
      if (standardDimensions.contains(dimension.key)) {
        dimensionOrder += dimension.key.token;
      }
    }
    return dimensionOrder;
  }

  /**
   * Sorts list of existing dimensions by increasing bytesInc, beginning with X
   * and Y, ending with stage position
   * 
   * @param coreIndex
   */
  private void sortDimensions(int coreIndex) {
    List<Dimension> dims = dimensions.get(coreIndex);
    dims.sort((Dimension dim1, Dimension dim2) -> Long.compare(dim1.bytesInc, dim2.bytesInc));

    // move X and Y to the start
    Dimension dimX = getDimension(coreIndex, DimensionKey.X);
    Dimension dimY = getDimension(coreIndex, DimensionKey.Y);
    dimensions.get(coreIndex).remove(dimX);
    dimensions.get(coreIndex).remove(dimY);

    // XY
    if (dimX.bytesInc < dimY.bytesInc) {
      dimensions.get(coreIndex).add(0, dimX);
      dimensions.get(coreIndex).add(1, dimY);
    } else {
      // YX
      dimensions.get(coreIndex).add(0, dimY);
      dimensions.get(coreIndex).add(1, dimX);
    }

    // move dimension S to the end to sort images by stage position, since tiles are
    // accessed as separate series
    Dimension dimS = getDimension(coreIndex, DimensionKey.S);
    dimensions.get(coreIndex).remove(dimS);
    dimensions.get(coreIndex).add(dimS);
  }

  public ArrayList<Dimension> getDimensions(int imageIndex) {
    sortDimensions(imageIndex);
    return dimensions.get(imageIndex);
  }

  /**
   * Adds Z, T and S dimension if they haven't been added already
   * 
   * @param imageIndex
   */
  public void addMissingDimensions(int imageIndex) {
    dimensions.get(imageIndex).sort((dim1, dim2) -> Long.compare(dim1.bytesInc, dim2.bytesInc));
    Dimension lastDimension = dimensions.get(imageIndex).get(dimensions.get(imageIndex).size() - 1);
    if (getDimension(imageIndex, DimensionKey.Z) == null) {
      addDimension(imageIndex,
          new Dimension(DimensionKey.Z, 1, lastDimension.bytesInc, "m", 1.0, lastDimension.oldPhysicalSize));
    }
    if (getDimension(imageIndex, DimensionKey.T) == null) {
      addDimension(imageIndex,
          new Dimension(DimensionKey.T, 1, lastDimension.bytesInc, "s", 1.0, lastDimension.oldPhysicalSize));
    }
    if (getDimension(imageIndex, DimensionKey.S) == null) {
      addDimension(imageIndex,
          new Dimension(DimensionKey.S, 1, lastDimension.bytesInc, "", 1.0, lastDimension.oldPhysicalSize));
    }
  }

  /**
   * Adds channel dimension
   * 
   * @param coreIndex
   * @param sizeC
   *          total number of channels
   * @param bytesInc
   */
  public void addChannelDimension(int coreIndex) {
    boolean rgb = (getDimension(coreIndex, DimensionKey.X).bytesInc % 3) == 0;
    int sizeC = rgb ? channels.get(coreIndex).size() / 3 : channels.get(coreIndex).size();

    long channelBytesInc = getChannelDimensionBytesInc(coreIndex);

    addDimension(coreIndex, Dimension.createChannelDimension(sizeC, channelBytesInc));
  }

  private long getChannelDimensionBytesInc(int coreIndex) {
    boolean rgb = (getDimension(coreIndex, DimensionKey.X).bytesInc % 3) == 0;
    long maxBytesInc = 0;

    if (rgb) {
      for (int i = 0; i < channels.get(coreIndex).size(); i++) {
        Channel channel = channels.get(coreIndex).get(i);
        if (channel.channelTag == 3) {
          maxBytesInc = channel.bytesInc > maxBytesInc ? channel.bytesInc : maxBytesInc;
        }
      }
    } else {
      for (Channel channel : channels.get(coreIndex)) {
        maxBytesInc = channel.bytesInc > maxBytesInc ? channel.bytesInc : maxBytesInc;
      }
    }

    if (maxBytesInc == 0) {
      Dimension yDim = getDimension(coreIndex, DimensionKey.Y);
      maxBytesInc = yDim.bytesInc * yDim.size;
    }
    return maxBytesInc;
  }

  public Detector getDetectorForFilter(int series, Filter filter) {
    for (DetectorSetting setting : detectorSettings.get(series)) {
      if (setting.sequenceIndex == filter.sequenceIndex && setting.detectorListIndex == filter.multibandIndex)
        return setting.detector;
    }
    return null;
  }

  public DetectorSetting getDetectorSetting(int series, int sequenceIndex, int detectorListIndex) {
    for (DetectorSetting setting : detectorSettings.get(series)) {
      if (setting.sequenceIndex == sequenceIndex && setting.detectorListIndex == detectorListIndex)
        return setting;
    }

    return null;
  }

  // -- Helper functions --
  private <T> ArrayList<ArrayList<T>> ArrayListOfArrayLists(int rows, Class<T> type) {
    ArrayList<ArrayList<T>> lst = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      lst.add(new ArrayList<T>());
    }
    return lst;
  }

  private <T, S> ArrayList<HashMap<T, S>> ArrayListOfHashMaps(int rows, Class<T> key, Class<S> value) {
    ArrayList<HashMap<T, S>> lst = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      lst.add(new HashMap<T, S>());
    }
    return lst;
  }
}
