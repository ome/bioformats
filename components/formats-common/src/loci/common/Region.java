/*
 * #%L
 * Common package for I/O and related utilities
 * %%
 * Copyright (C) 2005 - 2015 Open Microscopy Environment:
 *   - Board of Regents of the University of Wisconsin-Madison
 *   - Glencoe Software, Inc.
 *   - University of Dundee
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package loci.common;

/**
 * A class for representing a rectangular region.
 * This class is very similar to {@link java.awt.Rectangle};
 * it mainly exists to avoid problems with AWT, JNI and headless operation.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/Region.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/Region.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class Region {

  // -- Fields --

  public int x;
  public int y;
  public int width;
  public int height;

  // -- Constructors --

  public Region() {
  }

  public Region(int x, int y, int w, int h) {
    this.x = x;
    this.y = y;
    this.width = w;
    this.height = h;
  }

  // -- Region API --

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
      (tw < tx || tw > rx) && (th < ty || th > ry));
    return rtn;
  }

  /**
   * Returns a Region representing the intersection of this Region with the
   * given Region.  If the two Regions do not intersect, the result is an
   * empty Region.
   */
  public Region intersection(Region r) {
    int x = Math.max(this.x, r.x);
    int y = Math.max(this.y, r.y);
    int w = Math.min(this.x + this.width, r.x + r.width) - x;
    int h = Math.min(this.y + this.height, r.y + r.height) - y;

    if (w < 0) w = 0;
    if (h < 0) h = 0;

    return new Region(x, y, w, h);
  }

  /**
   * Returns true if the point specified by the given X and Y coordinates
   * is contained within this region.
   */
  public boolean containsPoint(int xc, int yc) {
    return intersects(new Region(xc, yc, 1, 1));
  }

  public String toString() {
    return "x=" + x + ", y=" + y + ", w=" + width + ", h=" + height;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Region)) return false;

    Region that = (Region) o;
    return this.x == that.x && this.y == that.y && this.width == that.width &&
      this.height == that.height;
  }

  public int hashCode() {
    return toString().hashCode();
  }

}
