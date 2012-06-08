/*
 * #%L
 * OME Bio-Formats package for reading and converting biological file formats.
 * %%
 * Copyright (C) 2005 - 2012 Open Microscopy Environment:
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

package loci.formats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import loci.common.DataTools;
import loci.common.Region;
import loci.formats.meta.IMetadata;
import loci.formats.meta.MetadataStore;

/**
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/TileStitcher.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/TileStitcher.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class TileStitcher extends ReaderWrapper {

  // -- Fields --

  private int tileX = 0;
  private int tileY = 0;

  private Integer[][] tileMap;

  // -- Utility methods --

  /** Converts the given reader into a TileStitcher, wrapping if needed. */
  public static TileStitcher makeTileStitcher(IFormatReader r) {
    if (r instanceof TileStitcher) return (TileStitcher) r;
    return new TileStitcher(r);
  }

  // -- Constructor --

  /** Constructs a TileStitcher around a new image reader. */
  public TileStitcher() { super(); }

  /** Constructs a TileStitcher with the given reader. */
  public TileStitcher(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getSizeX() */
  public int getSizeX() {
    return reader.getSizeX() * tileX;
  }

  /* @see IFormatReader#getSizeY() */
  public int getSizeY() {
    return reader.getSizeY() * tileY;
  }

  /* @see IFormatReader#getSeriesCount() */
  public int getSeriesCount() {
    if (tileX == 1 && tileY == 1) {
      return reader.getSeriesCount();
    }
    return 1;
  }

  /* @see IFormatReader#openBytes(int) */
  public byte[] openBytes(int no) throws FormatException, IOException {
    return openBytes(no, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return openBytes(no , buf, 0, 0, getSizeX(), getSizeY());
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    int bpp = FormatTools.getBytesPerPixel(getPixelType());
    int ch = getRGBChannelCount();
    byte[] newBuffer = DataTools.allocate(w, h, ch, bpp);
    return openBytes(no, newBuffer, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);

    if (tileX == 1 && tileY == 1) {
      return super.openBytes(no, buf, x, y, w, h);
    }

    byte[] tileBuf = new byte[buf.length / tileX * tileY];

    int tw = reader.getSizeX();
    int th = reader.getSizeY();

    Region image = new Region(x, y, w, h);
    int pixelType = getPixelType();
    int pixel = getRGBChannelCount() * FormatTools.getBytesPerPixel(pixelType);
    int outputRowLen = w * pixel;
    int outputRow = 0, outputCol = 0;
    Region intersection = null;

    for (int ty=0; ty<tileY; ty++) {
      for (int tx=0; tx<tileX; tx++) {
        Region tile = new Region(tx * tw, ty * th, tw, th);

        if (!tile.intersects(image)) {
          continue;
        }

        intersection = tile.intersection(image);
        int rowLen = pixel * (int) Math.min(intersection.width, tw);

        if (tileMap[ty][tx] == null) {
          outputCol += rowLen;
          continue;
        }

        reader.setSeries(tileMap[ty][tx]);
        reader.openBytes(no, tileBuf, 0, 0, tw, th);

        int outputOffset = outputRowLen * outputRow + outputCol;

        for (int row=0; row<intersection.height; row++) {
          int realRow = row + intersection.y - tile.y;
          int inputOffset = pixel * (realRow * tw + tx);
          System.arraycopy(tileBuf, inputOffset, buf, outputOffset, rowLen);
          outputOffset += outputRowLen;
        }

        outputCol += rowLen;
      }

      if (intersection != null) {
        outputRow += intersection.height;
        outputCol = 0;
      }
    }

    return buf;
  }

  /* @see IFormatReader#setId(String) */
  public void setId(String id) throws FormatException, IOException {
    super.setId(id);

    MetadataStore store = getMetadataStore();

    if (!(store instanceof IMetadata) || reader.getSeriesCount() == 1) {
      tileX = 1;
      tileY = 1;
      return;
    }

    IMetadata meta = (IMetadata) store;

    // don't even think about stitching HCS data, as it quickly gets complicated
    //
    // it might be worth improving this in the future so that fields are
    // stitched, but plates/wells are left alone, but for now it is easy
    // enough to just ignore HCS data
    if (meta.getPlateCount() > 0) {
      tileX = 1;
      tileY = 1;
      return;
    }

    // now make sure that all of the series have the same dimensions
    boolean equalDimensions = true;
    for (int i=1; i<meta.getImageCount(); i++) {
      if (!meta.getPixelsSizeX(i).equals(meta.getPixelsSizeX(0))) {
        equalDimensions = false;
      }
      if (!meta.getPixelsSizeY(i).equals(meta.getPixelsSizeY(0))) {
        equalDimensions = false;
      }
      if (!meta.getPixelsSizeZ(i).equals(meta.getPixelsSizeZ(0))) {
        equalDimensions = false;
      }
      if (!meta.getPixelsSizeC(i).equals(meta.getPixelsSizeC(0))) {
        equalDimensions = false;
      }
      if (!meta.getPixelsSizeT(i).equals(meta.getPixelsSizeT(0))) {
        equalDimensions = false;
      }
      if (!meta.getPixelsType(i).equals(meta.getPixelsType(0))) {
        equalDimensions = false;
      }
      if (!equalDimensions) break;
    }

    if (!equalDimensions) {
      tileX = 1;
      tileY = 1;
      return;
    }

    ArrayList<TileCoordinate> tiles = new ArrayList<TileCoordinate>();

    ArrayList<Double> uniqueX = new ArrayList<Double>();
    ArrayList<Double> uniqueY = new ArrayList<Double>();

    boolean equalZs = true;
    Double firstZ = meta.getPlanePositionZ(0, reader.getImageCount() - 1);

    for (int i=0; i<reader.getSeriesCount(); i++) {
      TileCoordinate coord = new TileCoordinate();
      coord.x = meta.getPlanePositionX(i, reader.getImageCount() - 1);
      coord.y = meta.getPlanePositionY(i, reader.getImageCount() - 1);

      tiles.add(coord);

      if (coord.x != null && !uniqueX.contains(coord.x)) {
        uniqueX.add(coord.x);
      }
      if (coord.y != null && !uniqueY.contains(coord.y)) {
        uniqueY.add(coord.y);
      }

      Double zPos = meta.getPlanePositionZ(i, reader.getImageCount() - 1);

      if (firstZ == null) {
        if (zPos != null) {
          equalZs = false;
        }
      }
      else {
        if (!firstZ.equals(zPos)) {
          equalZs = false;
        }
      }
    }

    tileX = uniqueX.size();
    tileY = uniqueY.size();

    if (!equalZs) {
      tileX = 1;
      tileY = 1;
      return;
    }

    tileMap = new Integer[tileY][tileX];

    Double[] xCoordinates = uniqueX.toArray(new Double[tileX]);
    Arrays.sort(xCoordinates);
    Double[] yCoordinates = uniqueY.toArray(new Double[tileY]);
    Arrays.sort(yCoordinates);

    for (int row=0; row<tileMap.length; row++) {
      for (int col=0; col<tileMap[row].length; col++) {
        TileCoordinate coordinate = new TileCoordinate();
        coordinate.x = xCoordinates[col];
        coordinate.y = yCoordinates[row];

        for (int tile=0; tile<tiles.size(); tile++) {
          if (tiles.get(tile).equals(coordinate)) {
            tileMap[row][col] = tile;
          }
        }
      }
    }
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#getNativeDataType() */
  public Class<?> getNativeDataType() {
    return byte[].class;
  }

  // -- Helper classes --

  class TileCoordinate {
    public Double x;
    public Double y;

    public boolean equals(Object o) {
      if (!(o instanceof TileCoordinate)) {
        return false;
      }
      TileCoordinate tile = (TileCoordinate) o;
      boolean xEqual = x == null ? tile.x == null : x.equals(tile.x);
      boolean yEqual = y == null ? tile.y == null : y.equals(tile.y);
      return xEqual && yEqual;
    }
  }

}
