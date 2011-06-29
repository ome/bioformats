//
// TileStitcher.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

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

package loci.formats;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

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
  private int[] tileOrdering;

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

  // -- TileStitcher API methods --

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
    return reader.getSeriesCount() / (tileX * tileY);
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
    byte[] newBuffer = new byte[w * h * ch * bpp];
    return openBytes(no, newBuffer, x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    FormatTools.assertId(getCurrentFile(), true, 2);

    byte[][][] bufs = new byte[tileX][tileY][buf.length / (tileX * tileY)];

    for (int ty=0; ty<tileY; ty++) {
      for (int tx=0; tx<tileX; tx++) {
        reader.setSeries(tileOrdering[ty * tileX + tx]);
        reader.openBytes(no, bufs[tx][ty], 0, 0, reader.getSizeX(), reader.getSizeY());

        int rowLen = bufs[tx][ty].length / reader.getSizeY();
        int offset = rowLen * (ty * reader.getSizeY() * tileX + tx);
        for (int row=0; row<reader.getSizeY(); row++) {
          System.arraycopy(bufs[tx][ty], row * rowLen, buf, offset, rowLen);
          offset += rowLen * tileX;
        }
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

    if (meta.getPlateCount() > 0) {
      tileX = 1;
      tileY = 1;
      return;
    }

    HashMap<TileCoordinate, Integer> tileMap =
      new HashMap<TileCoordinate, Integer>();

    for (int i=0; i<reader.getSeriesCount(); i++) {
      TileCoordinate coord = new TileCoordinate();
      coord.x = meta.getPlanePositionX(i, 0);
      coord.y = meta.getPlanePositionY(i, 0);

      if (!tileMap.containsKey(coord)) {
        tileMap.put(coord, i);
      }
      else {
        tileX = 1;
        tileY = 1;
        return;
      }
    }

    tileOrdering = new int[tileMap.size()];
    TileCoordinate[] tiles =
      tileMap.keySet().toArray(new TileCoordinate[tileMap.size()]);
    Arrays.sort(tiles, new TileComparator());

    Double firstX = tiles[0].x;

    for (int i=0; i<tiles.length; i++) {
      tileOrdering[i] = tileMap.get(tiles[i]);
      if (i > 0 && tileX == 0 && tiles[i].x.equals(firstX)) {
        tileX = i;
        tileY = tiles.length / tileX;
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
      return x.equals(tile.x) && y.equals(tile.y);
    }
  }

  class TileComparator implements Comparator {
    public int compare(Object o1, Object o2) {
      if (!(o1 instanceof TileCoordinate) || !(o2 instanceof TileCoordinate)) {
        return 0;
      }

      TileCoordinate t1 = (TileCoordinate) o1;
      TileCoordinate t2 = (TileCoordinate) o2;

      if (t1.equals(t2)) {
        return 0;
      }

      if (t1.y.equals(t2.y)) {
        return t1.x.compareTo(t2.x);
      }

      return t1.y.compareTo(t2.y);
    }
  }

}
