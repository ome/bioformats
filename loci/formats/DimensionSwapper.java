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

import java.io.IOException;

/** Handles swapping the dimension order of a file. */
public class DimensionSwapper extends ReaderWrapper {

  // -- Constructors -- 

  /** Constructs a DimensionSwapper around a new image reader. */
  public DimensionSwapper() { super(); }

  /** Constructs a DimensionSwapper with the given reader. */
  public DimensionSwapper(IFormatReader r) { super(r); }

  // -- IFormatReader API methods --

  /* @see loci.formats.IFormatReader#getSizeX(String) */
  public int getSizeX(String id) throws FormatException, IOException {
    return getCoreMetadata(id).sizeX[getSeries(id)]; 
  }

  /* @see loci.formats.IFormatReader#getSizeY(String) */
  public int getSizeY(String id) throws FormatException, IOException {
    return getCoreMetadata(id).sizeY[getSeries(id)]; 
  }
  
  /* @see loci.formats.IFormatReader#getSizeZ(String) */
  public int getSizeZ(String id) throws FormatException, IOException {
    return getCoreMetadata(id).sizeZ[getSeries(id)]; 
  }

  /* @see loci.formats.IFormatReader#getSizeC(String) */
  public int getSizeC(String id) throws FormatException, IOException {
    return getCoreMetadata(id).sizeC[getSeries(id)]; 
  }

  /* @see loci.formats.IFormatReader#getSizeT(String) */
  public int getSizeT(String id) throws FormatException, IOException {
    return getCoreMetadata(id).sizeT[getSeries(id)]; 
  }

  /* @see loci.formats.IFormatReader#getDimensionOrder(String) */
  public String getDimensionOrder(String id) 
    throws FormatException, IOException 
  {
    return getCoreMetadata(id).currentOrder[getSeries(id)]; 
  }

  // -- DimensionSwapper API methods --

  /** 
   * Swaps the dimensions according to the given dimension order.  If the given
   * order is identical to the file's native order, then nothing happens.
   * Note that this method will throw an exception if X and Y do not appear in
   * positions 0 and 1 (although X and Y can be reversed).
   */
  public void swapDimensions(String id, String order) 
    throws FormatException, IOException 
  {
    if (order == null) return;
    
    String oldOrder = getDimensionOrder(id); 
    
    if (order.equals(oldOrder)) return;

    int[] dims = new int[5];

    int xndx = oldOrder.indexOf("X");
    int yndx = oldOrder.indexOf("Y");
    int zndx = oldOrder.indexOf("Z");
    int cndx = oldOrder.indexOf("C");
    int tndx = oldOrder.indexOf("T");

    dims[xndx] = getSizeX(id);
    dims[yndx] = getSizeY(id);
    dims[zndx] = getSizeZ(id);
    dims[cndx] = getSizeC(id);
    dims[tndx] = getSizeT(id);

    int series = getSeries(id);
    CoreMetadata core = getCoreMetadata(id);

    core.sizeX[series] = dims[order.indexOf("X")];
    core.sizeY[series] = dims[order.indexOf("Y")];
    core.sizeZ[series] = dims[order.indexOf("Z")];
    core.sizeC[series] = dims[order.indexOf("C")];
    core.sizeT[series] = dims[order.indexOf("T")];
    core.currentOrder[series] = order;

    MetadataStore store = getMetadataStore(id);
    store.setPixels(new Integer(dims[xndx]), new Integer(dims[yndx]), 
      new Integer(dims[zndx]), new Integer(dims[cndx]), 
      new Integer(dims[tndx]), null, null, order, new Integer(series), null);
  }

}
