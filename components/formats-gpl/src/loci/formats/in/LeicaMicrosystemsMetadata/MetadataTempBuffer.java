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

  public Double[] pinholes, zSteps, tSteps, lensNA;
  public Double[][] expTimes, gains;
  public String[][] channelNames;
  public Double[][] exWaves;
  public ArrayList<ArrayList<Boolean>> activeDetector;

  public String[] corrections;
  public Length[] posX, posY, posZ;
  public Double[] refractiveIndex;
  public ArrayList<ArrayList<Length>> cutIns, cutOuts;
  public ArrayList<ArrayList<String>> filterModels;
  public ArrayList<ArrayList<Double>> laserIntensity, laserWavelength;
  public ArrayList<ArrayList<Boolean>> laserActive, laserFrap;

  public String[] imageNames;

  public int[] tileCount;
  public long[] tileBytesInc;
  public boolean[] inverseRgb; // true if channels are in BGR order

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
    lensNA = new Double[len];
    corrections = new String[len];
    posX = new Length[len];
    posY = new Length[len];
    posZ = new Length[len];
    refractiveIndex = new Double[len];
    zSteps = new Double[len];
    tSteps = new Double[len];
    pinholes = new Double[len];
    expTimes = new Double[len][];
    gains = new Double[len][];
    channelNames = new String[len][];
    exWaves = new Double[len][];
    imageNames = new String[len];
    inverseRgb = new boolean[len];

    laserWavelength = ArrayListOfArrayLists(len, Double.class);
    activeDetector = ArrayListOfArrayLists(len, Boolean.class);
    cutIns = ArrayListOfArrayLists(len, Length.class);
    cutOuts = ArrayListOfArrayLists(len, Length.class);
    filterModels = ArrayListOfArrayLists(len, String.class);
    laserIntensity = ArrayListOfArrayLists(len, Double.class);
    laserActive = ArrayListOfArrayLists(len, Boolean.class);
    laserFrap = ArrayListOfArrayLists(len, Boolean.class);
  }

  // -- Helper functions --
  private <T> ArrayList<ArrayList<T>> ArrayListOfArrayLists(int rows, Class<T> type) {
    ArrayList<ArrayList<T>> lst = new ArrayList<>();
    for (int i = 0; i < rows; i++) {
      lst.add(new ArrayList<T>());
    }
    return lst;
  }
}
