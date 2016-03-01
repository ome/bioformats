/*
 * #%L
 * Common package for I/O and related utilities
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

package loci.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Case insensitive variant of Location.
 */
public class CaseInsensitiveLocation extends Location {

  // Fields

  private static final Cache cache = new Cache();

  // -- Constructors (no caching) --

  public CaseInsensitiveLocation(String pathname) throws IOException {
    super(findCaseInsensitive(new Location(pathname)));
  }

  public CaseInsensitiveLocation(Location file) throws IOException {
    super(findCaseInsensitive(file));
  }

  public CaseInsensitiveLocation(File file) throws IOException {
    super(findCaseInsensitive(new Location(file.getAbsolutePath())));
  }

  public CaseInsensitiveLocation(String parent, String child) throws IOException {
    super(findCaseInsensitive(new Location(parent, child)));
  }

  public CaseInsensitiveLocation(CaseInsensitiveLocation parent, String child) throws IOException {
    super(findCaseInsensitive(new Location(parent.getAbsolutePath(), child)));
  }

  // -- Methods --

  /**
   * Remove (invalidate) cached content for all directories.
   */
  public static void invalidateCache() {
    cache.invalidate();
  }

  /**
   * Remove (invalidate) cached content for the specified directory.
   * @param dir the directory to remove,
   */
  public static void invalidateCache(File dir) {
    cache.invalidate(new Location(dir.getAbsolutePath()));
  }

  private static String findCaseInsensitive(Location name) throws IOException {
    // The file we're looking for doesn't exist, so look for it in the
    // same directory in a case-insensitive manner.  Note that this will
    // throw an exception if multiple copies are found.
    return cache.lookup(name.getAbsoluteFile()).getAbsolutePath();
  }

  // Helper class

  /**
   * Caching for CaseInsensitiveLocation.  A case insensitive path lookup
   * requires a full scan of the containing directory, which can be very
   * expensive.  This class caches insensitive-to-sensitive name mappings,
   * so the correct casing on the filesystem is returned.
   */
  private static final class Cache {

    /**
     * Mapping of directory names to directory content, the content being
     * a mapping of case insensitive name to case sensitive (real) name
     * on disc.
     */
    private HashMap<String, HashMap<String, String>> cache =
      new HashMap<String, HashMap<String, String>>();

    /**
     * The constructor.
     */
    public Cache() {
      super();
    }

    /**
     * Fill the cache with the content for the specified directory.
     * @param dir the directory to cache.
     * @return the filename mappings for the directory, or null if the
     * directory did not exist.
     */
    // Cache the whole directory content in a single pass
    private HashMap<String, String> fill(Location dir) throws IOException {
      String dirname = dir.getAbsolutePath();
      HashMap<String, String> s = cache.get(dirname);
      if (s == null && dir.exists()) {
        s = new HashMap<String, String>();
        cache.put(dirname, s);

        String[] files = dir.list();
        for (String name : files) {
          String lower = name.toLowerCase();
          if (s.containsKey(lower)) {
            throw new IOException("Multiple files found for case-insensitive path");
          }
          s.put(lower, name);
        }
      }
      return s;
    }

    /**
     * Remove a directory from the cache.
     * @param dir the directory to remove.
     */
    public void invalidate(Location dir) {
      String dirname = dir.getAbsolutePath();
      cache.remove(dirname);
    }

    /**
     * Remove all content from the cache.
     */
    public void invalidate() {
      cache.clear();
    }

    /**
     * Look up a filename in the cache.
     * @param name the name to look up (case insensitive).
     * @return the filename on disc (case sensitive).
     * @throws IOException
     */
    public Location lookup(Location name) throws IOException {
      Location parent = name.getParentFile();
      if (parent != null) {
        HashMap<String, String> s = fill(parent);

        if (s != null) {
          String realname = name.getName();
          String lower = realname.toLowerCase();
          String f = s.get(lower);
          if (f != null) {
            return new Location(parent, f);
          }
        }
      }
      return name;
    }
  }
}
