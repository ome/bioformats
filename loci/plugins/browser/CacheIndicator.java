//
// CacheIndicator.java
//

/*
LOCI 4D Data Browser plugin for quick browsing of 4D datasets in ImageJ.
Copyright (C) 2005-@year@ Christopher Peterson, Francis Wong, Curtis Rueden
and Melissa Linkert.

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

package loci.plugins.browser;

import javax.swing.*;
import java.awt.*;
import loci.formats.cache.*;

public class CacheIndicator extends JComponent {

  // -- Constants --

  private static final int COMPONENT_HEIGHT = 5;

  // -- Fields --

  private Cache cache;
  private int cacheLength, axis, numAxes;
  private JScrollBar scroll;
  private boolean doUpdate;

  // -- Constructor --

  public CacheIndicator(JScrollBar scroll) {
    doUpdate = true;
    this.scroll = scroll;
    setBackground(Color.WHITE);
    cache = null;
    cacheLength = 0;
    axis = -1;
  }

  // -- CacheIndicator API methods --

  public void setIndicator(Cache cache, int length, int axis) {
    doUpdate = false;
    this.cache = cache;
    cacheLength = length;
    this.axis = axis;

    // NB: think more on whether this should be synchronized (cache)
    numAxes = cache.getCurrentPos().length;

    doUpdate = true;
    repaint();
  }

  // -- JComponent API methods --

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!doUpdate) return;

    g.setColor(Color.BLACK);
    g.drawRect(0, 0, getWidth() - 1, COMPONENT_HEIGHT - 1);

    if (cacheLength == 0) return;

    int pixelsPerIndex = (getWidth() - 2) / (cacheLength + 1);
    int remainder = (getWidth() - 2) - (pixelsPerIndex * cacheLength);

    try {
      int[] currentPos = null;
      int[][] loadList = null;

      // NB: think more on whether this should be synchronized (cache)
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

        // NB: think more on whether this should be synchronized (cache)
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
    catch (CacheException e) { }
  }

  // -- Component API methods --

  public Dimension getPreferredSize() {
    return new Dimension(scroll.getPreferredSize().width, COMPONENT_HEIGHT);
  }

  public Dimension getMinimumSize() {
    return new Dimension(scroll.getMinimumSize().width, COMPONENT_HEIGHT);
  }

  public Dimension getMaximumSize() {
    return new Dimension(scroll.getMaximumSize().width, COMPONENT_HEIGHT);
  }

}
