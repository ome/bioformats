//
// CaseInsensitiveLocation.java
//

/*
LOCI Common package: utilities for I/O, reflection and miscellaneous tasks.
Copyright (C) 2005-@year@ Melissa Linkert, Curtis Rueden and Chris Allan.

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

package ome.scifio.io;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Case insensitive variant of Location.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/CaseInsensitiveLocation.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/CaseInsensitiveLocation.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CaseInsensitiveLocation extends Location {

  // Fields

  private static final Cache cache = new Cache();

  // -- Constructors (no caching) --

  public CaseInsensitiveLocation(String pathname) throws IOException {
    super(findCaseInsensitive(new File(pathname)));
  }

  public CaseInsensitiveLocation(File file) throws IOException {
    super(findCaseInsensitive(file));
  }

  public CaseInsensitiveLocation(String parent, String child) throws IOException {
    super(findCaseInsensitive(new File(parent + File.separator + child)));
  }

  public CaseInsensitiveLocation(CaseInsensitiveLocation parent, String child) throws IOException {
    super(findCaseInsensitive(new File(parent.getAbsolutePath(), child)));
  }


  // -- Methods --

  /**
   * Remove (invalidate) cached content all directories.
   */
  public static void invalidateCache() {
    cache.invalidate();
  }

  /**
   * Remove (invalidate) cached content for the specified directory.
   * @param dir the directory to remove,
   */
  public static void invalidateCache(File dir) {
    cache.invalidate(dir);
  }

  private static File findCaseInsensitive(File name) throws IOException{
    // The file we're looking for doesn't exist, so look for it in the
    // same directory in a case-insensitive manner.  Note that this will
    // throw an exception if multiple copies are found.
    return cache.lookup(name);
  }

  // Helper class

  /**
   * Caching for CaseInsensitiveLocation.  A case insensitive path lookup
   * requires a full scan of the containing directory, which can be very
   * expensive.  This class caches insensitive-to-sensitive name mappings,
   * so the correct casing on filesystem is returned.
   *
   * <dl><dt><b>Source code:</b></dt>
   * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/Cache.java">Trac</a>,
   * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/Cache.java;hb=HEAD">Gitweb</a></dd></dl>
   */
  private static final class Cache {

    /**
     * Mapping of directory names to directory content, the content being
     * a mapping of case insensitive name to case sensitive (real) name
     * on disc.
     */
    private HashMap<String,HashMap<String,String>> cache = new HashMap<String,HashMap<String,String>>();

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
    private HashMap<String,String> fill(File dir) throws IOException {
      String dirname = dir.getAbsolutePath();
      HashMap<String,String> s = cache.get(dirname);
      if (s == null && dir.exists()) {
        s = new HashMap<String,String>();
        cache.put(dirname, s);

        String[] files = dir.list();
        for (String name : files) {
          String lower = name.toLowerCase();
          if (s.containsKey(lower))
            throw new IOException("Multiple files found for case-insensitive path");
          s.put(lower, name);
        }
      }
      return s;
    }

    /**
     * Remove a directory from the cache.
     * @param dir the directory to remove.
     */
    public void invalidate(File dir) {
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
    public File lookup(File name) throws IOException {

      File parent = name.getParentFile();
      if (parent != null) {
        HashMap<String,String> s = fill(parent);

        if (s != null) {
          String realname = name.getName();
          String lower = realname.toLowerCase();
          String f = s.get(lower);
          if (f != null)
            return new File(parent, f);
        }
      }
      return name;
    }

  }

}
