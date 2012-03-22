//
// VirtualCursor.java
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

import net.imglib2.AbstractCursor;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * This class manages read only nonspatial access to a virtual image. Data
 * returned from get() can be written to but any changes are never saved
 * to disk.
 *
 * @author Barry DeZonia
 */
public class VirtualCursor<T extends NativeType<T> & RealType<T>>
  extends AbstractCursor<T>
{
  private VirtualImg<T> virtImage;
  private IntervalIterator iter;
  private long[] position;
  private VirtualAccessor<T> accessor;

  public VirtualCursor(VirtualImg<T> image) {
    super(image.numDimensions());
    this.virtImage = image;
    long[] fullDimensions = new long[image.numDimensions()];
    image.dimensions(fullDimensions);
    this.iter = new IntervalIterator(fullDimensions);
    this.position = new long[fullDimensions.length];
    this.accessor = new VirtualAccessor<T>(virtImage);
  }

  public T get() {
    iter.localize(position);
    return accessor.get(position);
  }

  public void fwd() {
    iter.fwd();
  }

  public void reset() {
    iter.reset();
  }

  public boolean hasNext() {
    return iter.hasNext();
  }

  public void localize(long[] pos) {
    iter.localize(pos);
  }

  public long getLongPosition(int d) {
    return iter.getLongPosition(d);
  }

  @Override
  public VirtualCursor<T> copy() {
    return new VirtualCursor<T>(virtImage);
  }

  @Override
  public VirtualCursor<T> copyCursor() {
    return new VirtualCursor<T>(virtImage);
  }

  public Object getCurrentPlane() {
    return accessor.getCurrentPlane();
  }
}
