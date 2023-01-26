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

import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Detector;
import loci.formats.in.LeicaMicrosystemsMetadata.model.DetectorSetting;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Filter;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Laser;
import loci.formats.in.LeicaMicrosystemsMetadata.model.ROI;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;

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
