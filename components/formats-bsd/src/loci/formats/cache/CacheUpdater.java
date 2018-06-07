/*
 * #%L
 * BSD implementations of Bio-Formats readers and writers
 * %%
 * Copyright (C) 2005 - 2017 Open Microscopy Environment:
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

package loci.formats.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread responsible for updating the cache
 * (loading and dropping planes) in the background.
 */
public class CacheUpdater extends Thread {

  // -- Constants --

  private static final Logger LOGGER =
    LoggerFactory.getLogger(CacheUpdater.class);

  // -- Fields --

  private Cache cache;
  private boolean quit;

  // -- Constructors --

  public CacheUpdater(Cache cache) {
    super("Bio-Formats-Cache-Updater");
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
      LOGGER.info("Thread interrupted", exc);
    }
  }

  // -- Thread API methods --

  @Override
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
      LOGGER.info("", e);
    }
  }

}
