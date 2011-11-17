//
// TwoChannelColorSpace.java
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

package loci.formats.gui;

import java.awt.color.ColorSpace;

/**
 * ColorSpace for 2-channel images.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/TwoChannelColorSpace.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/TwoChannelColorSpace.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class TwoChannelColorSpace extends ColorSpace {

  // -- Constants --

  public static final int CS_2C = -1;

  private static final int NUM_COMPONENTS = 2;

  // -- Constructor --

  protected TwoChannelColorSpace(int type, int components) {
    super(type, components);
  }

  // -- ColorSpace API methods --

  public float[] fromCIEXYZ(float[] color) {
    ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    return rgb.fromCIEXYZ(toRGB(color));
  }

  public float[] fromRGB(float[] rgb) {
    return new float[] {rgb[0], rgb[1]};
  }

  public static ColorSpace getInstance(int colorSpace) {
    if (colorSpace == CS_2C) {
      return new TwoChannelColorSpace(ColorSpace.TYPE_2CLR, NUM_COMPONENTS);
    }
    return ColorSpace.getInstance(colorSpace);
  }

  public String getName(int idx) {
    return idx == 0 ? "Red" : "Green";
  }

  public int getNumComponents() {
    return NUM_COMPONENTS;
  }

  public int getType() {
    return ColorSpace.TYPE_2CLR;
  }

  public boolean isCS_sRGB() { return false; }

  public float[] toCIEXYZ(float[] color) {
    ColorSpace rgb = ColorSpace.getInstance(ColorSpace.CS_sRGB);
    return rgb.toCIEXYZ(toRGB(color));
  }

  public float[] toRGB(float[] color) {
    return new float[] {color[0], color[1], 0};
  }

}
