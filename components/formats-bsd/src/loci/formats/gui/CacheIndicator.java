/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2016 Open Microscopy Environment:
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

  @Override
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

  @Override
  public Dimension getPreferredSize() {
    int w = comp == null ? COMPONENT_WIDTH : comp.getPreferredSize().width;
    return new Dimension(w, COMPONENT_HEIGHT);
  }

  @Override
  public Dimension getMinimumSize() {
    int w = comp == null ? COMPONENT_WIDTH : comp.getMinimumSize().width;
    return new Dimension(w, COMPONENT_HEIGHT);
  }

  @Override
  public Dimension getMaximumSize() {
    int w = comp == null ? Integer.MAX_VALUE : comp.getMaximumSize().width;
    return new Dimension(w, COMPONENT_HEIGHT);
  }

  // -- CacheListener API methods --

  @Override
  public void cacheUpdated(CacheEvent e) {
    if (e.getSource() instanceof Cache) this.cache = (Cache) e.getSource();
    int type = e.getType();
    if (type == CacheEvent.OBJECT_LOADED || type == CacheEvent.OBJECT_DROPPED ||
      !(e.getSource() instanceof Cache))
    {
      // cache has changed; update GUI
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {
          repaint();
        }
      });
    }
  }

}
