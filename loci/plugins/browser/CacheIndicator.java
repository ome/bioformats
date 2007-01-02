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

  private static final int COMPONENT_HEIGHT = 5;
  private static final boolean DEBUG = false;

  protected int [] cache,loadList;
  protected int cacheLength;
  protected double ratio;
  protected JScrollBar scroll;

  public CacheIndicator(JScrollBar scroll) {
    this.scroll = scroll;
    setBackground(Color.white);
    cache = null;
    loadList = null;
    cacheLength = 0;
  }

  public void setIndicator(int [] someCache, int [] someLoadList, int length) {
    cache = someCache;
    loadList = someLoadList;
    cacheLength = length - 1;
    int setRatio = translate(0);
    repaint();
  }

  public void paint(Graphics g) {
    g.setColor(Color.white);
    g.fillRect(0,0,getWidth()-1,COMPONENT_HEIGHT - 1);
    g.setColor(Color.black);
    g.drawRect(0,0,getWidth()-1,COMPONENT_HEIGHT - 1);

    if(ratio < 1 && ratio != 0) {
      int [] loadCount = new int[getWidth()];
      int [] cacheCount = new int[getWidth()];

      for(int i = 0;i < loadCount.length;i++) {
        loadCount[i] = 0;
        cacheCount[i] = 0;
      }

      //find how many entries of the cache are handled per pixel of indicator
      int perPixel;
      double integers = 2;
      double dPerPixel = ratio * integers;

      if(DEBUG) System.out.println("Ratio: " + ratio);
      while(dPerPixel < 1) {
        integers++;
        dPerPixel = ratio * integers;
      }

      Double temp = new Double(integers);
      perPixel = temp.intValue();

      if(DEBUG) System.out.println("PerPixel: " + perPixel);

      int colorAmount = 255 / perPixel;
      if(DEBUG) System.out.println("ColorAmount: " + colorAmount);

      for(int i = 0;i<loadList.length;i++) {
        boolean isLoaded = false;
        if( Arrays.binarySearch(cache, loadList[i]) >= 0) isLoaded = true;
        int index = translate(loadList[i]);
        if(!isLoaded) loadCount[index]++;
      }
      for(int i = 0;i<cache.length;i++) {
        int index = translate(cache[i]);
        cacheCount[index]++;
      }

      for(int i = 0;i < getWidth();i++) {
        int loadColor,cacheColor;
        loadColor = colorAmount * loadCount[i];
        cacheColor = colorAmount * cacheCount[i];
        if(loadColor != 0 || cacheColor != 0) {
          g.setColor(new Color(loadColor,0,cacheColor));
          g.drawLine(i,1,i,COMPONENT_HEIGHT - 2);
        }
      }
    }
    else if (ratio >= 1) {
      int prevLoad = -1;
      int startLoad = -1;
      g.setColor(Color.red);
      for(int i = 0;i<loadList.length;i++) {
        int toLoad = loadList[i];

        //correct for bug with length 1
        if(loadList.length == 1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }

        if(startLoad == -1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }
        else if(toLoad == prevLoad + 1 && startLoad != -1 && i != loadList.length - 1) {
          prevLoad = toLoad;
        }
        else {
          prevLoad = prevLoad + 1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x,1,wid,COMPONENT_HEIGHT - 2);
          startLoad = -1;
        }
      }

      prevLoad = -1;
      startLoad = -1;
      g.setColor(Color.blue);
      for(int i = 0;i<cache.length;i++) {
        int toLoad = cache[i];

        //correct for bug with length 1
        if(loadList.length == 1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }

        if(startLoad == -1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }
        else if(toLoad == prevLoad + 1 && startLoad != -1 && i != cache.length - 1) {
          prevLoad = toLoad;
        }
        else {
          prevLoad = prevLoad + 1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x,1,wid,COMPONENT_HEIGHT - 2);
          startLoad = -1;
        }
      }
    }
  }

  private int translate(int cacheIndex) {
    Integer width = new Integer(scroll.getWidth());
    double compSize = width.doubleValue();
    if(cacheLength == 0) return -1;
    Integer length = new Integer(cacheLength);
    double cLength = length.doubleValue();
    Integer thisIndex = new Integer(cacheIndex);
    double dIndex = thisIndex.doubleValue();

    ratio = compSize/cLength;
    double dPixel = ratio * dIndex;

    Double pixel = new Double(dPixel);
    return pixel.intValue();
  }

  public Dimension getPreferredSize() {
    return new Dimension(scroll.getWidth(),COMPONENT_HEIGHT);
  }

}
