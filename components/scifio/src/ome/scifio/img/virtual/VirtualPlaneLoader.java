/*
Copyright (c) 2011, Barry DeZonia.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package ome.scifio.img.virtual;

import ome.scifio.img.ImgIOUtils;
import loci.common.DataTools;
import loci.formats.FormatTools;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;


/**
 *
 * This class is responsible for loading one plane of data from an image
 * using an IFormatReader. The loading is done in a virtual fashion with
 * planes loaded when a desired position has not been loaded. The data is
 * loaded into a PlanarImg provided at construction time.
 *
 * @author Barry DeZonia
 *
 */
public class VirtualPlaneLoader {

  // -- instance variables --

  private VirtualImg<?> virtImage;
  private PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg;
  private long[] planeDims;
  private long[] planePosLoaded;
  private boolean bytesOnly;

  // -- constructor --

  /**
   * Create a VirtualPlaneLoader on a VirtualImage swapping planes into a
   * PlanarImg as needed.
   * @param image - the VirtualImg to load planes from
   * @param planeImg - the PlanarImg to load planes into
   * @param bytesOnly - a flag which defines whether planes passed around
   *     as byte[]'s only or as other primitive array types (int[]'s, etc.).
   */
  public VirtualPlaneLoader(VirtualImg<?> image,
    PlanarImg<?, ? extends ArrayDataAccess<?>> planeImg, boolean bytesOnly)
  {
    this.virtImage = image;
    this.planeImg = planeImg;
    this.planeDims = new long[image.numDimensions()-2];
    for (int i = 0; i < planeDims.length; i++)
      this.planeDims[i] = image.dimension(i+2);
    this.planePosLoaded = new long[planeDims.length];
    this.bytesOnly = bytesOnly;
    loadPlane(new long[image.numDimensions()]);
  }

  // -- public interface --

  /**
   * Makes sure the plane that contains the data of the given position is
   * loaded into the PlanarImg. Only reads data from VirtualImg if not
   * already loaded.
   */
  public boolean virtualSwap(long[] pos) {
    if (!planeLoaded(pos)) {
      loadPlane(pos);
      return true;
    }
    return false;
  }

  /**
   * Always loads the plane that contains the data of the given position.
   */
  @SuppressWarnings({"rawtypes","unchecked"})
  public void loadPlane(long[] pos) {
    for (int i = 0; i < planePosLoaded.length; i++)
      planePosLoaded[i] = pos[i+2];
    int planeNum = planeIndex(planeDims, planePosLoaded);
    byte[] planeBytes = null;
    try {
      planeBytes = virtImage.getReader().openBytes(planeNum);
    } catch (Exception e) {
      throw new IllegalArgumentException("cannot load plane "+planeNum);
    }
    Object primitivePlane;
    if (bytesOnly) {
      primitivePlane = planeBytes;
    }
    else { // want type from encoded bytes
      primitivePlane = typeConvert(planeBytes);
    }
    ArrayDataAccess<?> wrappedPlane = ImgIOUtils.makeArray(primitivePlane);
    ((PlanarImg)planeImg).setPlane(0, wrappedPlane);
  }

  // -- private helpers --

  private boolean planeLoaded(long[] pos) {
    for (int i = 2; i < pos.length; i++)
      if (pos[i] != planePosLoaded[i-2])
        return false;
    return true;
  }

  private static int planeIndex(long[] planeDimensions, long[] planePos) {
    int index = 0;
    for (int i = 0; i < planePos.length; i++) {
      int delta = 1;
      for (int j = 1; j <= i; j++)
        delta *= planeDimensions[j-1];
      index += delta * planePos[i];
    }
    return index;
  }

  private Object typeConvert(byte[] bytes) {

    int bytesPerPix;
    boolean floating;

    switch (virtImage.getReader().getPixelType()) {
      case FormatTools.UINT8:
      case FormatTools.INT8:
        bytesPerPix = 1;
        floating = false;
        break;
      case FormatTools.UINT16:
      case FormatTools.INT16:
        bytesPerPix = 2;
        floating = false;
        break;
      case FormatTools.UINT32:
      case FormatTools.INT32:
        bytesPerPix = 4;
        floating = false;
        break;
      case FormatTools.FLOAT:
        bytesPerPix = 4;
        floating = true;
        break;
      case FormatTools.DOUBLE:
        bytesPerPix = 8;
        floating = true;
        break;
      // TODO - BF does not support long[] i.e. FormatTools.INT64
      default:
        throw new
          IllegalArgumentException(
            "Cannot convert byte[] to unknown pixel type "+
              virtImage.getReader().getPixelType());
    }

    return DataTools.makeDataArray(bytes, bytesPerPix, floating, virtImage.getReader().isLittleEndian());
  }
}
