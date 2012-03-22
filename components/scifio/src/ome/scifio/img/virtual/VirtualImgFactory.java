//
// VirtualImgFactory.java
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

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

/**
 * Dummy implementation of an ImgFactory for VirtualImgs. Does not actually
 * do anything. Needed by VirtualImg to satisfy the Img contract.
 *
 * @author Barry DeZonia
 */
public class VirtualImgFactory<T extends NativeType<T> & RealType<T>>
  extends ImgFactory<T>
{
  @Override
  public Img<T> create(long[] dim, T type) {
    throw new UnsupportedOperationException(
      "VirtualImgFactories cannot actually create images");
  }

  @Override
  public <S> ImgFactory<S> imgFactory(S type) throws IncompatibleTypeException{
    throw new UnsupportedOperationException(
      "VirtualImgFactories cannot actually create images");
  }

}
