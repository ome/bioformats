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

import javax.swing.*;
import java.awt.*;

public class CacheIndicator extends JComponent
{
  public static final int COMPONENT_HEIGHT = 3;
  protected int [] cache,loadList;
  protected int cacheLength;
  double ratio;
  
  public CacheIndicator() {
    setBackground(Color.white);
    cache = null;
    loadList = null;
    cacheLength = 0;
  }

  public void setIndicator(int [] someCache, int [] someLoadList, int length) {
    cache = someCache;
    loadList = someLoadList;
    cacheLength = length;
    repaint();
  }

  public void paint(Graphics g) {
    g.setColor(Color.black);
    g.drawRect(0,0,getWidth(),COMPONENT_HEIGHT);
    if(ratio < 1) {
    }
    else {
      int prevLoad = -1;
      int startLoad = -1;
      g.setColor(Color.red);
      for(int i = 0;i<loadList.length;i++) {
        int toLoad = loadList[i];
        if(startLoad == -1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }
        else if(toLoad == prevLoad + 1 && startLoad != -1) {
          prevLoad = toLoad;
        }
        else {
          startLoad = -1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x,0,wid,COMPONENT_HEIGHT);
        }
      }
      prevLoad = -1;
      startLoad = -1;
      g.setColor(Color.blue);
      for(int i = 0;i<cache.length;i++) {
        int toLoad = cache[i];
        if(startLoad == -1) {
          startLoad = toLoad;
          prevLoad = toLoad;
        }
        else if(toLoad == prevLoad + 1 && startLoad != -1) {
          prevLoad = toLoad;
        }
        else {
          startLoad = -1;
          int x = translate(startLoad);
          int wid = translate(prevLoad) - x;
          g.fillRect(x,0,wid,HEIGHT);
        }
      }
    }
  }
  
  private int translate(int cacheIndex) {
    Integer width = new Integer(getWidth());
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
    return new Dimension(0,COMPONENT_HEIGHT);
  }
}