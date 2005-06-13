//
// WindowState.java
//

/*
VisBio application for visualization of multidimensional
biological image data. Copyright (C) 2002-2005 Curtis Rueden.

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

package loci.visbio;

import java.awt.Window;
import loci.visbio.util.SwingUtil;

/** A class containing a window's state. */
public class WindowState {

  // -- Fields --

  /** Title of the window. */
  protected String name;

  /** Visibility of the window. */
  protected boolean visible;

  /** Position of the window. */
  protected int x, y;

  /** Dimensions of the window. */
  protected int width, height;


  // -- Constructor --

  /** Creates a new window state object. */
  public WindowState(String name, boolean visible,
    int x, int y, int width, int height)
  {
    this.name = name;
    this.visible = visible;
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }


  // -- WindowInfo API methods --

  /** Applies this state to the given window. */
  public void applyTo(Window w) {
    SwingUtil.setWindowTitle(w, name);
    if (hasPosition()) w.setLocation(x, y);
    if (hasSize()) w.setSize(width, height);
    w.setVisible(visible);
  }

  /** Whether the window has a specified position. */
  public boolean hasPosition() { return x >= 0 && y >= 0; }

  /** Whether the window has a specified size. */
  public boolean hasSize() { return width > 0 && height > 0; }

  /** Gets window name. */
  public String getName() { return name; }

  /** Gets window visibility. */
  public boolean getVisible() { return visible; }

  /** Gets window X coordinate. */
  public int getX() { return x; }

  /** Gets window Y coordinate. */
  public int getY() { return y; }

  /** Gets window width. */
  public int getWidth() { return width; }

  /** Gets window height. */
  public int getHeight() { return height; }


  // -- Object API methods --

  /** Gets a string representation of this window state. */
  public String toString() {
    return name + ": visible=" + visible +
      ", x=" + x + ", y=" + y + ", width=" + width + ", height=" + height;
  }

}
