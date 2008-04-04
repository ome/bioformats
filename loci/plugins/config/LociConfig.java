//
// LociConfig.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Data Browser, Stack Colorizer,
Stack Slicer, and OME plugins. Copyright (C) 2005-@year@ Melissa Linkert,
Curtis Rueden, Christopher Peterson and Philip Huettl.

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU Library General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Library General Public License for more details.

You should have received a copy of the GNU Library General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package loci.plugins.config;

import ij.IJ;
import ij.ImageJ;
import ij.plugin.PlugIn;
import java.awt.*;

/**
 * An ImageJ plugin for displaying the LOCI plugins configuration dialog.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/config/LociConfig.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/config/LociConfig.java">SVN</a></dd></dl>
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class LociConfig implements PlugIn {

  // -- PlugIn API methods --

  /** Executes the plugin. */
  public void run(String arg) {
    ConfigWindow cw = new ConfigWindow();
    placeWindow(cw);
    cw.setVisible(true);
  }

  // -- Utility methods --

  /**
   * Places the given window at a nice location on screen, either centered
   * below the ImageJ window if there is one, or else centered on screen.
   */
  public static void placeWindow(Window w) {
    Dimension size = w.getSize();

    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

    ImageJ ij = IJ.getInstance();

    Point p = new Point();

    if (ij == null) {
      // center config window on screen
      p.x = (screen.width - size.width) / 2;
      p.y = (screen.height - size.height) / 2;
    }
    else {
      // place config window below ImageJ window
      Rectangle ijBounds = ij.getBounds();
      p.x = ijBounds.x + (ijBounds.width - size.width) / 2;
      p.y = ijBounds.y + ijBounds.height + 5;
    }

    // nudge config window away from screen edges
    final int pad = 10;
    if (p.x < pad) p.x = pad;
    else if (p.x + size.width + pad > screen.width) {
      p.x = screen.width - size.width - pad;
    }
    if (p.y < pad) p.y = pad;
    else if (p.y + size.height + pad > screen.height) {
      p.y = screen.height - size.height - pad;
    }

    w.setLocation(p);
  }

  // -- Main method --

  public static void main(String[] args) {
    new LociConfig().run(null);
  }

}
