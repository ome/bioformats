//
// VirtualAccessor.java
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

package ome.scifio.img.virtual;

import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.planar.PlanarImg;
import net.imglib2.img.planar.PlanarImgFactory;
import net.imglib2.img.planar.PlanarRandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * This class manages read only random access to a virtual image. The random
 * access position is provided by the user of the get(position) call. When user
 * tries to get a value from this accessor planes are loaded into memory as
 * needed. Planes are stored in a two dimensional PlanarImg that contains a
 * single plane. The user can modify the data values returned from get() but
 * the changes are never saved to disk.
 *
 * @author Barry DeZonia
 */
public class VirtualAccessor<T extends NativeType<T> & RealType<T>> {

  private PlanarImg<T, ? extends ArrayDataAccess<?>> planeImg;
  private PlanarRandomAccess<T> accessor;
  private VirtualPlaneLoader planeLoader;


  public VirtualAccessor(VirtualImg<T> virtImage) {
    long[] planeSize =
      new long[]{virtImage.dimension(0), virtImage.dimension(1)};
    this.planeImg =
      new PlanarImgFactory<T>().create(planeSize, virtImage.getType().copy());
    this.planeLoader =
      new VirtualPlaneLoader(virtImage, planeImg, virtImage.isByteOnly());
    // this initialization must follow loadPlane()
    this.accessor = planeImg.randomAccess();
  }

  public T get(long[] position) {

    if (planeLoader.virtualSwap(position)) {
      accessor.get().updateContainer(accessor);
    }

    accessor.setPosition(position[0], 0);
    accessor.setPosition(position[1], 1);

    return accessor.get();
  }

  public Object getCurrentPlane() {
    return planeImg.getPlane(0);
  }
}
