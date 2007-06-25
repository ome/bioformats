//
// DummyImageTransform.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-@year@ Curtis Rueden.

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

package loci.visbio.overlays;

import java.awt.image.BufferedImage;
import loci.visbio.data.ImageTransform;
import loci.visbio.data.DataTransform;

/** DummyImageTransform is a fake ImageTransform. */
public class DummyImageTransform extends ImageTransform{

  // -- Constants --

  /**  An arbitrary fake width. */
  public static final int WIDTH = 0;

  /**  An arbitrary fake height. */
  public static final int HEIGHT = 0;

  /** 
   * An arbitrary fake number of range components.  Matches the typical 
   * R,G,B,Alpha range.
   */
  public static final int RC = 4;

  /** Creates an image transform with the given transform as its parent. */
  public DummyImageTransform(DataTransform parent, String name) {
    this(parent, name, Double.NaN, Double.NaN, Double.NaN);
  }

  /**
   * Creates an image transform with the given transform as its parent,
   * that is dimensioned according to the specified values in microns.
   */
  public DummyImageTransform(DataTransform parent, String name,
    double width, double height, double step)
  {
    super(parent, name);
  }

  // -- ImageTransform API methods --

  /** Gets the width in pixels of each image. */
  public int getImageWidth(){ return WIDTH; } 

  /** Gets the height in pixels of each image. */
  public int getImageHeight() { return HEIGHT; }

  /** Gets number of range components at each pixel. */
  public int getRangeCount() { return RC; }

  /**
   * ImageTransform: "Obtains an image from the source(s) at the given
   * dimensional position." This version just returns blank images. 
   */
  public BufferedImage getImage(int[] pos) { 
    BufferedImage bi = new BufferedImage(getImageWidth(), getImageHeight(),
        BufferedImage.TYPE_INT_RGB);
    return bi;
  }

  // -- DataTransform API methods -- 

  /** 
   * DataTransform: "Gets whether this transform provides data of the given
   * dimensionality." Allow 2D and 3D for dummy version.
   */
  public boolean isValidDimension(int dim) {
    return (dim == 3 || dim == 2);
  }

  /**
   * DataTransform: "Gets a string id uniquely describing this data transform
   * at the given
   * dimensional position, for the purposes of thumbnail caching.
   * If global flag is true, the id is suitable for use in the default,
   * global cache file."
   * Returns arbitrary string "LOCI".
   */
  public String getCacheId(int[] pos, boolean global){ 
    return "LOCI";
  }

  // -- DataTransform methods overriden --

  /** Return a fake lengths array. */ 
  public int[] getLengths() { return new int[]{1}; }

}
