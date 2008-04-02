//
// CacheIndicator.java
//

/*
LOCI Bio-Formats package for reading and converting biological file formats.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden, Chris Allan,
Eric Kjellman and Brian Loranger.

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

package loci.formats.gui;

import javax.swing.*;
import java.awt.*;
import loci.formats.cache.*;

/**
 * Indicator GUI component showing which planes are currently in the cache
 * for a given dimensional axis at a particular dimensional position.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/gui/CacheIndicator.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/gui/CacheIndicator.java">SVN</a></dd></dl>
 */
public class CacheIndicator extends JComponent implements CacheListener {

  // -- Constants --

  private static final int COMPONENT_HEIGHT = 5;

  // -- Fields --

  private Component comp;
  private int axis;
  private Cache cache;

  // -- Constructor --

  /**
   * Creates a new cache indicator. The component was designed to sit directly
   * below an AWT Scrollbar or Swing JScrollBar, but any component can be
   * given, and the indicator will mimic its width (minus padding).
   */
  public CacheIndicator(Component c) {
    comp = c;
    axis = -1;
    setBackground(Color.WHITE);
  }

  // -- CacheIndicator API methods --

  public void setAxis(int axis) {
    this.axis = axis;
    repaint();
  }

  // -- JComponent API methods --

  public void paintComponent(Graphics g) {
//    super.paintComponent(g);
    g.setColor(Color.BLACK);
    g.drawRect(0, 0, getWidth() - 1, COMPONENT_HEIGHT - 1);

    int[] lengths = cache.getStrategy().getLengths();
    int cacheLength = axis >= 0 && axis < lengths.length ? lengths[axis] : 0;

    if (cacheLength == 0) return;

    int pixelsPerIndex = (getWidth() - 2) / (cacheLength + 1);
    int remainder = (getWidth() - 2) - (pixelsPerIndex * cacheLength);

    try {
      int[] currentPos = null;
      int[][] loadList = null;

      currentPos = cache.getCurrentPos();
      loadList = cache.getStrategy().getLoadList(currentPos);

      int[] pos = new int[currentPos.length];
      System.arraycopy(currentPos, 0, pos, 0, pos.length);

      int start = 1;
      for (int i=0; i<cacheLength; i++) {
        pos[axis] = i;

        boolean inLoadList = false;
        for (int j=0; j<loadList.length; j++) {
          boolean equal = true;
          for (int k=0; k<loadList[j].length; k++) {
            if (loadList[j][k] != pos[k]) {
              equal = false;
              break;
            }
          }
          if (equal) {
            inLoadList = true;
            break;
          }
        }

        boolean inCache = false;

        inCache = cache.isInCache(pos);

        if (inCache) g.setColor(Color.BLUE);
        else if (inLoadList) g.setColor(Color.RED);
        else g.setColor(Color.WHITE);
        int len = pixelsPerIndex;
        if (i < remainder) len++;
        g.fillRect(start, 1, len, getHeight() - 2);
        start += len;
      }
    }
    catch (CacheException e) { e.printStackTrace(); }
  }

  // -- Component API methods --

  public Dimension getPreferredSize() {
    return new Dimension(comp.getPreferredSize().width, COMPONENT_HEIGHT);
  }

  public Dimension getMinimumSize() {
    return new Dimension(comp.getMinimumSize().width, COMPONENT_HEIGHT);
  }

  public Dimension getMaximumSize() {
    return new Dimension(comp.getMaximumSize().width, COMPONENT_HEIGHT);
  }

  // -- CacheListener API methods --

  public void cacheUpdated(CacheEvent e) {
    this.cache = (Cache) e.getSource();
    int type = e.getType();
    if (type == CacheEvent.OBJECT_LOADED || type == CacheEvent.OBJECT_DROPPED) {
      // cache has changed; update GUI
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          repaint();
        }
      });
    }
  }

}
