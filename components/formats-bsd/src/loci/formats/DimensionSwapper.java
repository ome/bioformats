/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.formats;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import loci.formats.meta.MetadataStore;
import loci.formats.CoreMetadata;

/**
 * Handles swapping the dimension order of an image series. This class is
 * useful for both reassigning ZCT sizes (the input dimension order), and
 * shuffling around the resultant planar order (the output dimension order).
 */
public class DimensionSwapper extends ReaderWrapper {

  // -- Utility methods --

  /** Converts the given reader into a DimensionSwapper, wrapping if needed. */
  public static DimensionSwapper makeDimensionSwapper(IFormatReader r) {
    if (r instanceof DimensionSwapper) return (DimensionSwapper) r;
    return new DimensionSwapper(r);
  }

  // -- Fields --

  /** Core metadata associated with this dimension swapper. */
  private List<CoreMetadata> core;

  // -- Constructors --

  /** Constructs a DimensionSwapper around a new image reader. */
  public DimensionSwapper() { super(); }

  /** Constructs a DimensionSwapper with the given reader. */
  public DimensionSwapper(IFormatReader r) { super(r); }

  // -- DimensionSwapper API methods --

  /**
   * Sets the input dimension order according to the given string (e.g.,
   * "XYZCT"). This string indicates the planar rasterization order from the
   * source, overriding the detected order. It may result in the dimensional
   * axis sizes changing.
   *
   * If the given order is identical to the file's native order, then
   * nothing happens. Note that this method will throw an exception if X and Y
   * do not appear in positions 0 and 1 (although X and Y can be reversed).
   */
  public void swapDimensions(String order) {
    FormatTools.assertId(getCurrentFile(), true, 2);

    if (order == null) throw new IllegalArgumentException("order is null");

    String oldOrder = getInputOrder();
    if (order.equals(oldOrder)) return;

    if (order.length() != 5) {
      throw new IllegalArgumentException("order is unexpected length (" +
        order.length() + ")");
    }

    int newX = order.indexOf('X');
    int newY = order.indexOf('Y');
    int newZ = order.indexOf('Z');
    int newC = order.indexOf('C');
    int newT = order.indexOf('T');

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

    int oldX = oldOrder.indexOf('X');
    int oldY = oldOrder.indexOf('Y');
    int oldZ = oldOrder.indexOf('Z');
    int oldC = oldOrder.indexOf('C');
    int oldT = oldOrder.indexOf('T');

    if (oldC != newC && reader.getRGBChannelCount() > 1) {
      throw new IllegalArgumentException(
        "Cannot swap C dimension when RGB channel count > 1");
    }

    dims[oldX] = getSizeX();
    dims[oldY] = getSizeY();
    dims[oldZ] = getSizeZ();
    dims[oldC] = getSizeC();
    dims[oldT] = getSizeT();

    Modulo[] moduli = new Modulo[3];
    moduli[oldZ - 2] = getModuloZ();
    moduli[oldC - 2] = getModuloC();
    moduli[oldT - 2] = getModuloT();

    SwappableMetadata ms = (SwappableMetadata) core.get(getCoreIndex());

    ms.sizeX = dims[newX];
    ms.sizeY = dims[newY];
    ms.sizeZ = dims[newZ];
    ms.sizeC = dims[newC];
    ms.sizeT = dims[newT];

    ms.moduloZ = moduli[newZ - 2];
    ms.moduloC = moduli[newC - 2];
    ms.moduloT = moduli[newT - 2];

    ms.inputOrder = order;

    MetadataStore store = getMetadataStore();
    MetadataTools.populatePixels(store, this);
  }

  /**
   * Sets the output dimension order according to the given string (e.g.,
   * "XYZCT"). This string indicates the final planar rasterization
   * order&mdash;i.e., the mapping from 1D plane number to 3D (Z, C, T) tuple.
   * Changing it will not affect the Z, C or T sizes but will alter the order
   * in which planes are returned when iterating.
   *
   * This method is useful when your application requires a particular output
   * dimension order; e.g., ImageJ virtual stacks must be in XYCZT order.
   */
  public void setOutputOrder(String outputOrder) {
    FormatTools.assertId(getCurrentFile(), true, 2);
    core.get(getCoreIndex()).dimensionOrder = outputOrder;
  }

  public String getInputOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return ((SwappableMetadata) core.get(getCoreIndex())).inputOrder;
  }

  // -- IFormatReader API methods --

  /* @see IFormatReader#getSizeX() */
  @Override
  public int getSizeX() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core.get(getCoreIndex()).sizeX;
  }

  /* @see IFormatReader#getSizeY() */
  @Override
  public int getSizeY() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core.get(getCoreIndex()).sizeY;
  }

  /* @see IFormatReader#getSizeZ() */
  @Override
  public int getSizeZ() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core.get(getCoreIndex()).sizeZ;
  }

  /* @see IFormatReader#getSizeC() */
  @Override
  public int getSizeC() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core.get(getCoreIndex()).sizeC;
  }

  /* @see IFormatReader#getSizeT() */
  @Override
  public int getSizeT() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core.get(getCoreIndex()).sizeT;
  }

  /* @see IFormatReader#getDimensionOrder() */
  @Override
  public String getDimensionOrder() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core.get(getCoreIndex()).dimensionOrder;
  }

  /* @see IFormatReader#openBytes(int) */
  @Override
  public byte[] openBytes(int no) throws FormatException, IOException {
    return super.openBytes(reorder(no));
  }

  /* @see IFormatReader#openBytes(int, int, int, int, int) */
  @Override
  public byte[] openBytes(int no, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return super.openBytes(reorder(no), x, y, w, h);
  }

  /* @see IFormatReader#openBytes(int, byte[]) */
  @Override
  public byte[] openBytes(int no, byte[] buf)
    throws FormatException, IOException
  {
    return super.openBytes(reorder(no), buf);
  }

  /* @see IFormatReader#openBytes(int, byte[], int, int, int, int) */
  @Override
  public byte[] openBytes(int no, byte[] buf, int x, int y, int w, int h)
    throws FormatException, IOException
  {
    return super.openBytes(reorder(no), buf, x, y, w, h);
  }

  /* @see IFormatReader#openThumbImage(int) */
  @Override
  public byte[] openThumbBytes(int no) throws FormatException, IOException {
    return super.openThumbBytes(reorder(no));
  }

  /* @see IFormatReader#getZCTCoords(int) */
  @Override
  public int[] getZCTCoords(int no) {
    return FormatTools.getZCTCoords(this, no);
  }

  @Override
  public int[] getZCTModuloCoords(int index) {
    return FormatTools.getZCTModuloCoords(this, index);
  }

  /* @see IFormatReader#getIndex(int, int, int) */
  @Override
  public int getIndex(int z, int c, int t) {
    return FormatTools.getIndex(this, z, c, t);
  }

  @Override
  public int getIndex(int z, int c, int t, int moduloZ, int moduloC, int moduloT) {
      return FormatTools.getIndex(this, z, c, t, moduloZ, moduloC, moduloT);
  }

  /* @see IFormatReader#getCoreMetadataList() */
  @Override
  public List<CoreMetadata> getCoreMetadataList() {
    FormatTools.assertId(getCurrentFile(), true, 2);
    return core;
  }

  // -- IFormatHandler API methods --

  /* @see IFormatHandler#setId(String) */
  @Override
  public void setId(String id) throws FormatException, IOException {
    String oldFile = getCurrentFile();
    super.setId(id);
    if (!id.equals(oldFile) || core == null ||
      core.size() != reader.getCoreMetadataList().size())
    {
      // NB: Create our own copy of the CoreMetadata,
      // which we can manipulate safely.
      List<CoreMetadata> oldcore = reader.getCoreMetadataList();
      core = new ArrayList<CoreMetadata>();
      for (int s=0; s<oldcore.size(); s++) {
        SwappableMetadata swappable = new SwappableMetadata(reader, s);
        swappable.resolutionCount = oldcore.get(s).resolutionCount;
        core.add(swappable);
      }
    }
  }

  // -- Helper methods --
  
  protected int reorder(int no) {
    if (getInputOrder() == null) return no;
    return FormatTools.getReorderedIndex(getInputOrder(), getDimensionOrder(),
      getSizeZ(), getEffectiveSizeC(), getSizeT(), getImageCount(), no);
  }

}
