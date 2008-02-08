//
// CacheUpdater.java
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

package loci.formats.cache;

import loci.formats.LogTools;

/**
 * Thread responsible for updating the cache
 * (loading and dropping planes) in the background.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="https://skyking.microscopy.wisc.edu/trac/java/browser/trunk/loci/formats/cache/CacheUpdater.java">Trac</a>,
 * <a href="https://skyking.microscopy.wisc.edu/svn/java/trunk/loci/formats/cache/CacheUpdater.java">SVN</a></dd></dl>
 */
public class CacheUpdater extends Thread {

  // -- Fields --

  private Cache cache;
  private boolean quit;

  // -- Constructors --

  public CacheUpdater(Cache cache) {
    super("4D-Data-Browser-Cache-Updater");
    setPriority(Thread.MIN_PRIORITY);
    this.cache = cache;
    quit = false;
  }

  // -- CacheUpdater API methods --

  public void quit() {
    quit = true;
    // NB: Must wait for thread to die; Bio-Formats is not thread-safe, so
    // it would be bad for more than one CacheUpdater thread to try to use the
    // same IFormatReader at the same time.
    try {
      join();
    }
    catch (InterruptedException exc) {
      LogTools.trace(exc);
    }
  }

  // -- Thread API methods --

  public void run() {
    int length = 0;
    try {
      length = cache.getStrategy().getLoadList(cache.getCurrentPos()).length;
      for (int i=0; i<length; i++) {
        if (quit) break;
        cache.recache(i);
      }
    }
    catch (CacheException e) {
      LogTools.trace(e);
    }
  }

}
