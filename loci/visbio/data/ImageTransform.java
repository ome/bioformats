//
// ImageTransform.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2004 Curtis Rueden.

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

package loci.visbio.data;

import visad.RealType;

/**
 * ImageTransform is an interface used by DataTransforms
 * to indicate that they provide image data.
 */
public interface ImageTransform {

  /** Gets the width in pixels of each image. */
  int getImageWidth();

  /** Gets the height in pixels of each image. */
  int getImageHeight();

  /** Gets number of range components at each pixel. */
  int getRangeCount();

  /** Gets type associated with image X component. */
  RealType getXType();

  /** Gets type associated with image Y component. */
  RealType getYType();

  /** Gets types associated with image range components. */
  RealType[] getRangeTypes();

}
