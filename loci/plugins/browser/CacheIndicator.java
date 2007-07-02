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
import java.util.Arrays;

public class CacheIndicator extends JComponent {

  // -- Constants --

  private static final int COMPONENT_HEIGHT = 5;
  private static final boolean DEBUG = false;

  // -- Fields --

  protected int[] cache, loadList;
  protected int cacheLength;
  protected double ratio;
  protected JScrollBar scroll;
  protected boolean doUpdate;

  // -- Constructor --

  public CacheIndicator(JScrollBar scroll) {
    doUpdate = true;
    this.scroll = scroll;
    setBackground(Color.white);
    cache = null;
    loadList = null;
    cacheLength = 0;
  }

  // -- CacheIndicator API methods --

  public void setIndicator(int[] someCache, int[] someLoadList, int length) {
    doUpdate = false;
    cache = someCache;
    loadList = someLoadList;
    cacheLength = length;
    int setRatio = translate(0);
    doUpdate = true;
    repaint();
  }

  // -- JComponent API methods --

  public void paintComponent(Graphics g) {
    super.paintComponent(g);
    if (!doUpdate) return;

    g.setColor(Color.black);
    g.drawRect(0, 0, getWidth() - 1, COMPONENT_HEIGHT - 1);

    if (ratio < 1 && ratio != 0) {
      int[] loadCount = new int[getWidth()];
      int[] cacheCount = new int[getWidth()];

      for (int i=0; i<loadCount.length; i++) {
        loadCount[i] = 0;
        cacheCount[i] = 0;
      }

      // find how many entries of the cache are handled per pixel of indicator
      double integers = 2;
      double dPerPixel = ratio * integers;

      if (DEBUG) System.out.println("Ratio: " + ratio);
      while (dPerPixel < 1) {
        integers++;
        dPerPixel = ratio * integers;
      }

      int perPixel = (int) integers;

      if (DEBUG) System.out.println("PerPixel: " + perPixel);

      int colorAmount = 255 / perPixel;
      if (DEBUG) System.out.println("ColorAmount: " + colorAmount);

      for (int i=0; i<loadList.length; i++) {
        boolean isLoaded = false;
        if (Arrays.binarySearch(cache, loadList[i]) >= 0) isLoaded = true;
        int index = translate(loadList[i]);
        if (!isLoaded) loadCount[index]++;
      }
      for (int i=0; i<cache.length; i++) {
        int index = translate(cache[i]);
        cacheCount[index]++;
      }

      for (int i=0; i<getWidth(); i++) {
        int loadColor, cacheColor;
        loadColor = colorAmount * loadCount[i];
        cacheColor = colorAmount * cacheCount[i];
        if (loadColor != 0 || cacheColor != 0) {
          g.setColor(new Color(loadColor, 0, cacheColor));
          g.drawLine(i, 1, i, COMPONENT_HEIGHT - 2);
        }
      }
    }
    else if (ratio >= 1) {
      int prevLoad = -1;
      int startLoad = -1;
      g.setColor(Color.red);
      for (int i=0; i<loadList.length; i++) {
        if (!doUpdate) break;
        int toLoad = loadList[i];

        // correct for bug with length 1
        if (loadList.length == 1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }

        if (startLoad == -1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }
        else if (toLoad == prevLoad + 1 && startLoad != -1) {
          prevLoad = toLoad;
        }
        else if (toLoad != prevLoad + 1 &&
          startLoad != -1 && cache.length - 1 != i)
        {
          prevLoad = prevLoad + 1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x, 1, wid, COMPONENT_HEIGHT - 2);
          startLoad = -1;
        }

        if (i == loadList.length - 1) {
          prevLoad = prevLoad + 1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x, 1, wid, COMPONENT_HEIGHT - 2);
          startLoad = -1;
        }
      }

      prevLoad = -1;
      startLoad = -1;
      g.setColor(Color.blue);
      for (int i=0; i<cache.length; i++) {
        if (!doUpdate) break;
        int toLoad = cache[i];

        // correct for bug with length 1
        if (loadList.length == 1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }

        if (startLoad == -1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }
        else if (toLoad == prevLoad + 1 && startLoad != -1) {
          prevLoad = toLoad;
        }
        else if (toLoad != prevLoad + 1 &&
          startLoad != -1 && cache.length - 1 != i)
        {
          prevLoad = prevLoad + 1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x, 1, wid, COMPONENT_HEIGHT - 2);
          startLoad = -1;
        }

        if (i == cache.length - 1) {
          prevLoad = prevLoad + 1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x, 1, wid, COMPONENT_HEIGHT - 2);
          startLoad = -1;
        }
      }
    }
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

  // -- Helper methods --

  private int translate(int cacheIndex) {
    Integer width = new Integer(scroll.getWidth());
    double compSize = width.doubleValue();
    if (cacheLength == 0) return -1;
    Integer length = new Integer(cacheLength);
    double cLength = length.doubleValue();
    Integer thisIndex = new Integer(cacheIndex);
    double dIndex = thisIndex.doubleValue();

    ratio = compSize / cLength;
    double dPixel = ratio * dIndex;

    Double pixel = new Double(dPixel);
    return pixel.intValue();
  }

  // -- Main method --

  /** Tests the cache indicator GUI component. */
  public static void main(String[] args) {
    JFrame frame = new JFrame("Cache indicator test");
    JPanel pane = new JPanel();
    frame.setContentPane(pane);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));
    JScrollBar scroll = new JScrollBar(JScrollBar.HORIZONTAL);
    pane.add(scroll);
    int len = 2000;
    CacheIndicator[] ci = new CacheIndicator[len];
    for (int i=0; i<len; i++) {
      ci[i] = new CacheIndicator(scroll);
      pane.add(ci[i]);
    }
    frame.pack();
    frame.setBounds(100, 100, 500, frame.getHeight());
    frame.setVisible(true);
    for (int i=0; i<len; i++) {
      int[] cache = new int[len / 10];
      for (int j=i; j<i+len/10; j++) cache[j-i] = j % len;
      int[] load = new int[len / 10];
      for (int j=i+len/10; j<i+len/5; j++) load[j-i-len/10] = j % len;
      ci[i].setIndicator(cache, load, len);
    }
  }

}
