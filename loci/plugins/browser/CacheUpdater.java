//
// CacheUpdater.java
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
Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1G307  USA
*/

package loci.plugins.browser;

import loci.formats.LogTools;
import loci.formats.cache.*;

/**
 * Thread responsible for updating the cache
 * (loading and dropping planes) in the background.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/plugins/browser/CacheUpdater.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/plugins/browser/CacheUpdater.java">SVN</a></dd></dl>
 */
public class CacheUpdater extends Thread {

  // -- Fields --

  private Cache cache;
  private CacheIndicator[] indicators;
  private int[] lengths, axes;
  private boolean quit;

  // -- Constructor --

  public CacheUpdater(Cache cache, CacheIndicator[] indicators, int[] lengths,
    int[] axes)
  {
    super("4D-Data-Browser-Cache-Updater");
    setPriority(Thread.MIN_PRIORITY);
    this.cache = cache;
    this.indicators = indicators;
    this.lengths = lengths;
    this.axes = axes;
    quit = false;
  }

  // -- CacheUpdater API methods --

  public void quit() {
    quit = true;
  }

  // -- Thread API methods --

  public void run() {
    int length = 0;
    try {
      synchronized (cache) {
        length = cache.getStrategy().getLoadList(cache.getCurrentPos()).length;
      }
      for (int i=0; i<length; i++) {
        if (quit) break;
        synchronized (cache) {
          cache.recache(i);
        }
        for (int j=0; j<indicators.length; j++) {
          if (indicators[j] != null) {
            indicators[j].setIndicator(cache, lengths[j], axes[j]);
          }
        }
      }
    }
    catch (CacheException e) {
      LogTools.trace(e);
    }
  }

}
