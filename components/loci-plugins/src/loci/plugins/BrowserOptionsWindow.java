//
// BrowserOptionsWindow.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins;

import java.awt.Dimension;
import javax.swing.JFrame;
import loci.formats.cache.Cache;
import loci.formats.gui.CacheComponent;

/**
 * Extension of JFrame that allows the user to adjust caching settings.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/BrowserOptionsWindow.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/BrowserOptionsWindow.java">SVN</a></dd></dl>
 */
public class BrowserOptionsWindow extends JFrame {

  // -- Constructor --

  public BrowserOptionsWindow(String title, Cache cache) {
    super(title);

    CacheComponent panel =
      new CacheComponent(cache, new String[] {"C", "Z", "T"});

    panel.setMinimumSize(new Dimension(300, 500));
    setContentPane(panel);
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    pack();
  }

}
