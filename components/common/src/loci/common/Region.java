//
// Region.java
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

package loci.common;

/**
 * A class for representing a rectangular region.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/common/src/loci/common/Region.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/common/src/loci/common/Region.java">SVN</a></dd></dl>
 */
public class Region {

  // -- Fields --

  public int x;
  public int y;
  public int width;
  public int height;

  // -- Constructor --

  public Region(int x, int y, int width, int height) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  // -- Region API methods --

  /** Returns true if this region intersects the given region. */
  public boolean intersects(Region r) {
    int tw = this.width;
    int th = this.height;
    int rw = r.width;
    int rh = r.height;
    if (rw <= 0 || rh <= 0 || tw <= 0 || th <= 0) {
      return false;
    }
    int tx = this.x;
    int ty = this.y;
    int rx = r.x;
    int ry = r.y;
    rw += rx;
    rh += ry;
    tw += tx;
    th += ty;
    boolean rtn = ((rw < rx || rw > tx) && (rh < ry || rh > ty) &&
      (tx < tx || tw > rx) && (th < ty || th > ry));
    return rtn;
  }

  /**
   * Returns a Region representing the intersection of this Region with the
   * given Region.  If the two Regions do not intersect, the result is an
   * empty Region.
   */
  public Region intersection(Region r) {
    int x = (int) Math.max(this.x, r.x);
    int y = (int) Math.max(this.y, r.y);
    int w = (int) Math.min(this.x + this.width, r.x + r.width) - x;
    int h = (int) Math.min(this.y + this.height, r.y + r.height) - y;

    if (w < 0) w = 0;
    if (h < 0) h = 0;

    return new Region(x, y, w, h);
  }

  public String toString() {
    return "x=" + x + ", y=" + y + ", w=" + width + ", h=" + height;
  }

}
