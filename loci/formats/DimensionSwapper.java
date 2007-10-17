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

/**
 * Handles swapping the dimension order of a file.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/DimensionSwapper.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/DimensionSwapper.java">SVN</a></dd></dl>
 */
public class DimensionSwapper extends ReaderWrapper {

  // -- Constructors --

  /** Constructs a DimensionSwapper around a new image reader. */
  public DimensionSwapper() { super(); }

  /** Constructs a DimensionSwapper with the given reader. */
  public DimensionSwapper(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSizeX() */
  public int getSizeX() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return getCoreMetadata().sizeX[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeY() */
  public int getSizeY() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return getCoreMetadata().sizeY[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeZ() */
  public int getSizeZ() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return getCoreMetadata().sizeZ[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeC() */
  public int getSizeC() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return getCoreMetadata().sizeC[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getSizeT() */
  public int getSizeT() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return getCoreMetadata().sizeT[getSeries()];
  }

  /* @see loci.formats.IFormatReader#getDimensionOrder() */
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
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
    FormatTools.assertId(getCurrentFile(), true, 2);

    if (order == null) throw new IllegalArgumentException("order is null");

    String oldOrder = getDimensionOrder();
    if (order.equals(oldOrder)) return;

    if (order.length() != 5) {
      throw new IllegalArgumentException("order is unexpected length (" +
        order.length() + ")");
    }

    int newX = order.indexOf("X");
    int newY = order.indexOf("Y");
    int newZ = order.indexOf("Z");
    int newC = order.indexOf("C");
    int newT = order.indexOf("T");

    if (newX < 0) throw new IllegalArgumentException("X does not appear");
    if (newY < 0) throw new IllegalArgumentException("Y does not appear");
    if (newZ < 0) throw new IllegalArgumentException("Z does not appear");
    if (newC < 0) throw new IllegalArgumentException("C does not appear");
    if (newT < 0) throw new IllegalArgumentException("T does not appear");

    if (newX > 1) {
      throw new IllegalArgumentException("X in unexpected position (" +
        newX + ")");
    }
    if (newY > 1) {
      throw new IllegalArgumentException("Y in unexpected position (" +
        newY + ")");
    }

    int[] dims = new int[5];

    int oldX = oldOrder.indexOf("X");
    int oldY = oldOrder.indexOf("Y");
    int oldZ = oldOrder.indexOf("Z");
    int oldC = oldOrder.indexOf("C");
    int oldT = oldOrder.indexOf("T");

    dims[oldX] = getSizeX();
    dims[oldY] = getSizeY();
    dims[oldZ] = getSizeZ();
    dims[oldC] = getSizeC();
    dims[oldT] = getSizeT();

    int series = getSeries();
    CoreMetadata core = getCoreMetadata();

    core.sizeX[series] = dims[newX];
    core.sizeY[series] = dims[newY];
    core.sizeZ[series] = dims[newZ];
    core.sizeC[series] = dims[newC];
    core.sizeT[series] = dims[newT];
    core.currentOrder[series] = order;

    MetadataStore store = getMetadataStore();
    store.setPixels(new Integer(dims[newX]), new Integer(dims[newY]),
      new Integer(dims[newZ]), new Integer(dims[newC]),
      new Integer(dims[newT]), null, null, order, new Integer(series), null);
  }

}
