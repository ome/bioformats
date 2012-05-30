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

package loci.common;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Caching for CaseInsensitiveLocation.  A case insensitive path lookup
 * requires a full scan of the containing directory, which can be very
 * expensive.  This class caches insensitive-to-sensitive name mappings,
 * so the correct casing on filesystem is returned.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/CaseInsensitiveLocationCache.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/CaseInsensitiveLocationCache.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CaseInsensitiveLocationCache {
  
  private HashMap<String,HashMap<String,HashSet<String>>> cache = new HashMap<String,HashMap<String,HashSet<String>>>();
  public CaseInsensitiveLocationCache() {
    super();
  }

  // Cache the whole directory content in a single pass
  private HashMap<String,HashSet<String>> fill(File dir) {
    String dirname = dir.getAbsolutePath();
    HashMap<String,HashSet<String>> s = cache.get(dirname);
    if (s == null && dir.exists()) {
      s = new HashMap<String,HashSet<String>>();
      cache.put(dirname, s);
      String[] files = dir.list();
      for (String name : files) {
        String lower = name.toLowerCase();
        HashSet<String> f = s.get(lower);
        if (f == null) {
          f = new HashSet<String>();
          s.put(lower, f);
        }
        f.add(name);
      }
    }
    return s;
  }
    
  public File lookup(File name) throws IOException {

    File parent = name.getParentFile();
    if (parent != null) {
      HashMap<String,HashSet<String>> s = fill(parent);

      if (s != null) {
        String realname = name.getName();
        String lower = realname.toLowerCase();

        HashSet<String> f = s.get(lower);
        if (f != null) {
          if (f.size() > 1)
            throw new IOException("Multiple files found for case-insensitive path");
          else if (f.size() == 1) {
            String[] values = f.toArray(new String[0]);
            return new File(parent, values[0]);
          }
        }
      }
    }
    return name;
  }

}

