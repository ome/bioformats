//
// SignedColorModel.java
//

/*
OME Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ UW-Madison LOCI and Glencoe Software, Inc.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.formats;

import java.awt.image.*;
import java.io.IOException;

/**
 * ColorModel that handles 8, 16 and 32 bits per channel signed data.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/bio-formats/src/loci/formats/SignedColorModel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/bio-formats/src/loci/formats/SignedColorModel.java">SVN</a></dd></dl>
 */
public class SignedColorModel extends ColorModel {

  // -- Fields --

  private int pixelBits;
  private int nChannels;
  private ComponentColorModel helper;

  // -- Constructors --

  public SignedColorModel(int pixelBits, int dataType, int nChannels)
    throws IOException
  {
    super(pixelBits);

    helper = new ComponentColorModel(AWTImageTools.makeColorSpace(nChannels),
      nChannels == 4, false, ColorModel.TRANSLUCENT, dataType);

    this.pixelBits = pixelBits;
    this.nChannels = nChannels;
  }

  // -- ColorModel API methods --

  /* @see java.awt.image.ColorModel#getDataElements(int, Object) */
  public synchronized Object getDataElements(int rgb, Object pixel) {
    return helper.getDataElements(rgb, pixel);
  }

  /* @see java.awt.image.ColorModel#isCompatibleRaster(Raster) */
  public boolean isCompatibleRaster(Raster raster) {
    return helper.isCompatibleRaster(raster);
  }

  /* @see java.awt.image.ColorModel#createCompatibleWritableRaster(int, int) */
  public WritableRaster createCompatibleWritableRaster(int w, int h) {
    return helper.createCompatibleWritableRaster(w, h);
  }

  /* @see java.awt.image.ColorModel#getAlpha(int) */
  public int getAlpha(int pixel) {
    int alpha = helper.getAlpha(pixel);
    return rescale(alpha);
  }

  /* @see java.awt.image.ColorModel#getBlue(int) */
  public int getBlue(int pixel) {
    int blue = helper.getBlue(pixel);
    return rescale(blue);
  }

  /* @see java.awt.image.ColorModel#getGreen(int) */
  public int getGreen(int pixel) {
    int green = helper.getGreen(pixel);
    return rescale(green);
  }

  /* @see java.awt.image.ColorModel#getRed(int) */
  public int getRed(int pixel) {
    int red = helper.getRed(pixel);
    return rescale(red);
  }

  // -- Helper methods --

  private int rescale(int value) {
    if (value < 128) {
      value += 128; // [0, 127] -> [128, 255]
    }
    else {
      value -= 128; // [128, 255] -> [0, 127]
    }
    return value;
  }

}
