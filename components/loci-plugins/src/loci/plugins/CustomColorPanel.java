//
// CustomColorPanel.java
//

/*
LOCI Plugins for ImageJ: a collection of ImageJ plugins including the
Bio-Formats Importer, Bio-Formats Exporter, Bio-Formats Macro Extensions,
Data Browser, Stack Colorizer and Stack Slicer. Copyright (C) 2005-@year@
Melissa Linkert, Curtis Rueden and Christopher Peterson.

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

package loci.plugins;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Panel;


/**
 * TODO
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/components/loci-plugins/src/loci/plugins/CustomColorPanel.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/components/loci-plugins/src/loci/plugins/CustomColorPanel.java">SVN</a></dd></dl>
 *
 * @author Melissa Linkert linkert at wisc.edu
 */
public class CustomColorPanel extends Panel {
  
  private static final int WIDTH = 100, HEIGHT = 50;
  private Color c;

  public CustomColorPanel(Color c) {
    this.c = c;
  }

  public Dimension getPreferredSize() {
    return new Dimension(WIDTH, HEIGHT);
  }

  void setColor(Color c) { this.c = c; }

  public Dimension getMinimumSize() {
    return new Dimension(WIDTH, HEIGHT);
  }

  public void paint(Graphics g) {
    // CTR: can we switch this to an anonymous inner class in CustomColorChooser
    // by just using setBackgroundColor instead of needing this new color field?
    g.setColor(c);
    g.fillRect(0, 0, WIDTH, HEIGHT);
    g.setColor(Color.black);
    g.drawRect(0, 0, WIDTH-1, HEIGHT-1);
  }
  
}