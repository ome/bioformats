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

package loci.formats.in.LeicaMicrosystemsMetadata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.formats.in.LeicaMicrosystemsMetadata.helpers.Tuple;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Channel.ChannelType;
import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;
import ome.units.quantity.Length;

/**
 * This class bundles all image dimension information extracted from LMS image xmls.
 * 
 * @author Constanze Wendlandt constanze.wendlandt at leica-microsystems.com
 */
public class DimensionStore {
    public List<Dimension> dimensions = new ArrayList<Dimension>();
    public List<Channel> channels = new ArrayList<Channel>();
    public boolean rgb = false;
    public boolean inverseRgb = false;
    public double physicalSizeX;
    public double physicalSizeY;
    public double zBegin;
    public double zEnd;
    public double zWidePosition = 0;
    public double zGalvoPosition = 0;
    public List<Tuple<Length, Length, Length>> fieldPositions = new ArrayList<Tuple<Length,Length, Length>>();
    public boolean tilescanInfoHasZ = false;
    public boolean flipX;
    public boolean flipY;
    public boolean swapXY;
    public double zStep;
    public double tStep;
    public int tileCount = 1;
    public long tileBytesInc;
    public int extras = 1;

    public enum ZDriveMode {
      ZGalvo,
      ZWide
    }

    public ZDriveMode zDriveMode;

    /**
   * Inserts dimension and optionally adapts other dimension-dependent
   * values
   * 
   * @param imageIndex
   * @param dimension
   */
  public void addDimension(Dimension dimension) {
    dimensions.add(dimension);
    if (dimension.key == DimensionKey.X) {
      physicalSizeX = dimension.getLengthPerUnit();
    } else if (dimension.key == DimensionKey.Y) {
      physicalSizeY = dimension.getLengthPerUnit();
    } else if (dimension.key == DimensionKey.Z) {
      if (dimension.getLengthPerUnit() != null) {
        zStep = Math.abs(dimension.getLengthPerUnit());
      }
    } else if (dimension.key == DimensionKey.T){
      tStep = dimension.getLengthPerUnit();
    }
     else if (dimension.key == DimensionKey.S) {
      tileCount *= dimension.size;
      tileBytesInc = dimension.bytesInc;
    }
  }

  /**
   * Returns the dimension order as a string
   * 
   * @param imageIndex
   * @return dimension order string, as it is expected in
   *         CoreMetadata.dimensionOrder
   */
  public String getDimensionOrder() {
    sortDimensions();

    String dimensionOrder = "";
    List<DimensionKey> standardDimensions = new ArrayList<>(
        Arrays.asList(DimensionKey.X, DimensionKey.Y, DimensionKey.Z,
            DimensionKey.C, DimensionKey.T));

    for (Dimension dimension : dimensions) {
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
  private void sortDimensions() {
    List<Dimension> dims = dimensions;
    dims.sort((Dimension dim1, Dimension dim2) -> Long.compare(dim1.bytesInc, dim2.bytesInc));

    // move X and Y to the start
    Dimension dimX = getDimension(DimensionKey.X);
    Dimension dimY = getDimension(DimensionKey.Y);
    dimensions.remove(dimX);
    dimensions.remove(dimY);

    // XY
    if (dimX.bytesInc < dimY.bytesInc) {
      dimensions.add(0, dimX);
      dimensions.add(1, dimY);
    } else {
      // YX
      dimensions.add(0, dimY);
      dimensions.add(1, dimX);
    }

    // move dimension S to the end to sort images by stage position, since tiles are
    // accessed as separate series
    Dimension dimS = getDimension(DimensionKey.S);
    dimensions.remove(dimS);
    dimensions.add(dimS);
  }

  public List<Dimension> getDimensions() {
    sortDimensions();
    return dimensions;
  }

  /**
   * Adds Z, T and S dimension if they haven't been added already
   * 
   * @param imageIndex
   */
  public void addMissingDimensions() {
    dimensions.sort((dim1, dim2) -> Long.compare(dim1.bytesInc, dim2.bytesInc));
    Dimension lastDimension = dimensions.get(dimensions.size() - 1);
    if (getDimension(DimensionKey.Z) == null) {
      addDimension(new Dimension(DimensionKey.Z, 1, lastDimension.bytesInc, "m", 1.0, 0.0, lastDimension.oldPhysicalSize));
    }
    if (getDimension(DimensionKey.T) == null) {
      addDimension(new Dimension(DimensionKey.T, 1, lastDimension.bytesInc, "s", 1.0, 0.0, lastDimension.oldPhysicalSize));
    }
    if (getDimension(DimensionKey.S) == null) {
      addDimension(new Dimension(DimensionKey.S, 1, lastDimension.bytesInc, "", 1.0, 0.0, lastDimension.oldPhysicalSize));
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
  public void addChannelDimension() {
    boolean rgb = (getDimension(DimensionKey.X).bytesInc % 3) == 0;
    int sizeC = rgb ? channels.size() / 3 : channels.size();

    long channelBytesInc = getChannelDimensionBytesInc();

    addDimension(Dimension.createChannelDimension(sizeC, channelBytesInc));
  }

  private long getChannelDimensionBytesInc() {
    boolean rgb = (getDimension(DimensionKey.X).bytesInc % 3) == 0;
    long maxBytesInc = 0;

    if (rgb) {
      for (int i = 0; i < channels.size(); i++) {
        Channel channel = channels.get(i);
        if (channel.channelTag == 3) {
          maxBytesInc = channel.bytesInc > maxBytesInc ? channel.bytesInc : maxBytesInc;
        }
      }
    } else {
      for (Channel channel : channels) {
        maxBytesInc = channel.bytesInc > maxBytesInc ? channel.bytesInc : maxBytesInc;
      }
    }

    if (maxBytesInc == 0) {
      Dimension yDim = getDimension(DimensionKey.Y);
      maxBytesInc = yDim.bytesInc * yDim.size;
    }
    return maxBytesInc;
  }

  public Dimension getDimension(DimensionKey key) {
    for (Dimension dimension : dimensions) {
      if (dimension.key == key)
        return dimension;
    }
    return null;
  }

  /**
   * Calculates the number of planes per tile (= field position), considering CZT
   * @return
   */
  public int getNumberOfPlanesPerTile(){
    int zSize = getDimension(DimensionKey.Z).size;
    int tSize = getDimension(DimensionKey.T).size;
    return zSize * tSize * channels.size();
  }

  public void setChannels(List<Channel> channels){
    this.channels = channels;
    this.rgb = this.channels.get(0).channelType != ChannelType.MONO;

    // RGB order is defined by ChannelTag order (1,2,3 = RGB, 3,2,1=BGR)
    this.inverseRgb = this.rgb && this.channels.get(0).channelType == ChannelType.BLUE;
  }
}
