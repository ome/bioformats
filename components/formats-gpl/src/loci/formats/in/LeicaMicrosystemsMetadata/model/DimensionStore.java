package loci.formats.in.LeicaMicrosystemsMetadata.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import loci.formats.in.LeicaMicrosystemsMetadata.model.Dimension.DimensionKey;
import ome.units.quantity.Length;

public class DimensionStore {
    public List<Dimension> dimensions = new ArrayList<Dimension>();
    public List<Channel> channels = new ArrayList<Channel>();
    public double physicalSizeX;
    public double physicalSizeY;
    public List<Length> fieldPosXs = new ArrayList<Length>();
    public List<Length> fieldPosYs = new ArrayList<Length>();
    public double zStep;
    public int tileCount = 1;
    public long tileBytesInc;

    /**
   * Inserts dimension to buffer and optionally adapts other dimension-dependent
   * values
   * 
   * @param imageIndex
   * @param dimension
   */
  public void addDimension(Dimension dimension) {
    dimensions.add(dimension);
    if (dimension.key == DimensionKey.X) {
      physicalSizeX = dimension.getLength();
    } else if (dimension.key == DimensionKey.Y) {
      physicalSizeY = dimension.getLength();
    } else if (dimension.key == DimensionKey.Z) {
      if (dimension.getLength() != null) {
        zStep = Math.abs(dimension.getLength());
      }
    } else if (dimension.key == DimensionKey.S) {
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

  public List<Dimension> getDimensions(int imageIndex) {
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
      addDimension(new Dimension(DimensionKey.Z, 1, lastDimension.bytesInc, "m", 1.0, lastDimension.oldPhysicalSize));
    }
    if (getDimension(DimensionKey.T) == null) {
      addDimension(new Dimension(DimensionKey.T, 1, lastDimension.bytesInc, "s", 1.0, lastDimension.oldPhysicalSize));
    }
    if (getDimension(DimensionKey.S) == null) {
      addDimension(new Dimension(DimensionKey.S, 1, lastDimension.bytesInc, "", 1.0, lastDimension.oldPhysicalSize));
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
}
