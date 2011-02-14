//
// CacheIndicator.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import loci.formats.cache.Cache;
import loci.formats.cache.CacheEvent;
import loci.formats.cache.CacheException;
import loci.formats.cache.CacheListener;

/**
 * Indicator GUI component showing which planes are currently in the cache
 * for a given dimensional axis at a particular dimensional position.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/bio-formats/src/loci/formats/gui/CacheIndicator.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/bio-formats/src/loci/formats/gui/CacheIndicator.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CacheIndicator extends JComponent implements CacheListener {

  // -- Constants --

  private static final int COMPONENT_WIDTH = 5;
  private static final int COMPONENT_HEIGHT = 5;

  // -- Fields --

  private Cache cache;
  private int axis;
  private Component comp;
  private int lPad, rPad;

  // -- Constructor --

  /** Creates a new cache indicator for the given cache. */
  public CacheIndicator(Cache cache, int axis) {
    this(cache, axis, null, 0, 0);
  }

  /**
   * Creates a new cache indicator. The component was designed to sit directly
   * below an AWT Scrollbar or Swing JScrollBar, but any component can be
   * given, and the indicator will mimic its width (minus padding).
   */
  public CacheIndicator(Cache cache, int axis,
    Component comp, int lPad, int rPad)
  {
    this.cache = cache;
    this.axis = axis;
    this.comp = comp;
    this.lPad = lPad;
    this.rPad = rPad;
    cache.addCacheListener(this);
    setBackground(Color.WHITE);
  }

  // -- JComponent API methods --

  public void paintComponent(Graphics g) {
//    super.paintComponent(g);

    g.setColor(Color.BLACK);
    int xStart = lPad, width = getWidth() - lPad - rPad;
    g.drawRect(xStart, 0, width - 1, COMPONENT_HEIGHT - 1);

    int[] lengths = cache.getStrategy().getLengths();
    int cacheLength = axis >= 0 && axis < lengths.length ? lengths[axis] : 0;

    if (cacheLength == 0) return;

    int pixelsPerIndex = (width - 2) / cacheLength;
    int remainder = (width - 2) - (pixelsPerIndex * cacheLength);

    try {
      int[] currentPos = null;
      int[][] loadList = null;

      currentPos = cache.getCurrentPos();
      loadList = cache.getStrategy().getLoadList(currentPos);

      int[] pos = new int[currentPos.length];
      System.arraycopy(currentPos, 0, pos, 0, pos.length);

      int start = xStart + 1;
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
    int w = comp == null ? COMPONENT_WIDTH : comp.getPreferredSize().width;
    return new Dimension(w, COMPONENT_HEIGHT);
  }

  public Dimension getMinimumSize() {
    int w = comp == null ? COMPONENT_WIDTH : comp.getMinimumSize().width;
    return new Dimension(w, COMPONENT_HEIGHT);
  }

  public Dimension getMaximumSize() {
    int w = comp == null ? Integer.MAX_VALUE : comp.getMaximumSize().width;
    return new Dimension(w, COMPONENT_HEIGHT);
  }

  // -- CacheListener API methods --

  public void cacheUpdated(CacheEvent e) {
    if (e.getSource() instanceof Cache) this.cache = (Cache) e.getSource();
    int type = e.getType();
    if (type == CacheEvent.OBJECT_LOADED || type == CacheEvent.OBJECT_DROPPED ||
      !(e.getSource() instanceof Cache))
    {
      // cache has changed; update GUI
      SwingUtilities.invokeLater(new Runnable() {
        public void run() {
          repaint();
        }
      });
    }
  }

}
