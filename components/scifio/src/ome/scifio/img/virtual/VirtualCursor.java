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
 *
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
