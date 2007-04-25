//
// DimensionSwapper.java
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

package loci.formats;

/** Handles swapping the dimension order of a file. */
public class DimensionSwapper extends ReaderWrapper {

  // -- Constructors --

  /** Constructs a DimensionSwapper around a new image reader. */
  public DimensionSwapper() { super(); }

  /** Constructs a DimensionSwapper with the given reader. */
  public DimensionSwapper(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSizeX() */
  public int getSizeX() {
    return getCoreMetadata().sizeX[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeY() */
  public int getSizeY() {
    return getCoreMetadata().sizeY[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeZ() */
  public int getSizeZ() {
    return getCoreMetadata().sizeZ[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeC() */
  public int getSizeC() {
    return getCoreMetadata().sizeC[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeT() */
  public int getSizeT() {
    return getCoreMetadata().sizeT[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    return getCoreMetadata().currentOrder[getSeries()];
  }

  // -- DimensionSwapper API methods --

  /**
   * Swaps the dimensions according to the given dimension order.  If the given
   * order is identical to the file's native order, then nothing happens.
   * Note that this method will throw an exception if X and Y do not appear in
   * positions 0 and 1 (although X and Y can be reversed).
   */
  public void swapDimensions(String order) {
    if (order == null) return;

    String oldOrder = getDimensionOrder();

    if (order.equals(oldOrder)) return;

    int[] dims = new int[5];

    int xndx = oldOrder.indexOf("X");
    int yndx = oldOrder.indexOf("Y");
    int zndx = oldOrder.indexOf("Z");
    int cndx = oldOrder.indexOf("C");
    int tndx = oldOrder.indexOf("T");

    dims[xndx] = getSizeX();
    dims[yndx] = getSizeY();
    dims[zndx] = getSizeZ();
    dims[cndx] = getSizeC();
    dims[tndx] = getSizeT();

    int series = getSeries();
    CoreMetadata core = getCoreMetadata();

    core.sizeX[series] = dims[order.indexOf("X")];
    core.sizeY[series] = dims[order.indexOf("Y")];
    core.sizeZ[series] = dims[order.indexOf("Z")];
    core.sizeC[series] = dims[order.indexOf("C")];
    core.sizeT[series] = dims[order.indexOf("T")];
    core.currentOrder[series] = order;

    MetadataStore store = getMetadataStore();
    store.setPixels(new Integer(dims[xndx]), new Integer(dims[yndx]),
      new Integer(dims[zndx]), new Integer(dims[cndx]),
      new Integer(dims[tndx]), null, null, order, new Integer(series), null);
  }

}
