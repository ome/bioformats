//
// VirtualRandomAccess.java
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

import net.imglib2.AbstractRandomAccess;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * This class manages read only spatial access to a virtual image. Data
 * returned from get() can be written to but any changes are never saved
 * to disk.
 *
 * @author Barry DeZonia
 */
public class VirtualRandomAccess<T extends NativeType<T> & RealType<T>>
  extends AbstractRandomAccess<T>
{
  private VirtualImg<T> virtImage;
  private VirtualAccessor<T> accessor;

  /**
   * Constructor
   *
   * @param image - the VirtualImg to access randomly
   */
  public VirtualRandomAccess(VirtualImg<T> image)
  {
    super(image.numDimensions());
    this.accessor = new VirtualAccessor<T>(image);
    this.virtImage = image;
  }

  public void fwd(int d) {
    position[d]++;
  }

  public void bck(int d) {
    position[d]--;
  }

  public void move(long distance, int d) {
    position[d] += distance;
  }

  public void setPosition(int[] pos) {
    for (int i = 0; i < position.length; i++)
      position[i] = pos[i];
  }

  public void setPosition(long[] pos) {
    for (int i = 0; i < position.length; i++)
      position[i] = pos[i];
  }

  public void setPosition(long pos, int d) {
    position[d] = pos;
  }

  @Override
  public VirtualRandomAccess<T> copy() {
    return new VirtualRandomAccess<T>(virtImage);
  }

  @Override
  public VirtualRandomAccess<T> copyRandomAccess() {
    return new VirtualRandomAccess<T>(virtImage);
  }

  public T get() {
    return accessor.get(position);
  }

  public Object getCurrentPlane() {
    return accessor.getCurrentPlane();
  }
}
