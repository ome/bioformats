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

/**
 * Legacy delegator class for {@link ome.scifio.io.CaseInsensitiveLocation}
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/CaseInsensitiveLocation.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/CaseInsensitiveLocation.java;hb=HEAD">Gitweb</a></dd></dl>
 */
public class CaseInsensitiveLocation extends Location {

  // -- Constructors (no caching) --

  public CaseInsensitiveLocation(String pathname) throws IOException {
    super();
    loc = new ome.scifio.io.CaseInsensitiveLocation(pathname);
  }

  public CaseInsensitiveLocation(File file) throws IOException {
    super();
    loc = new ome.scifio.io.CaseInsensitiveLocation(file);
  }

  public CaseInsensitiveLocation(String parent, String child) throws IOException {
    super();
    loc = new ome.scifio.io.CaseInsensitiveLocation(parent, child);
  }

  public CaseInsensitiveLocation(CaseInsensitiveLocation parent, String child) throws IOException {
    super();
    loc = new ome.scifio.io.CaseInsensitiveLocation((ome.scifio.io.CaseInsensitiveLocation)parent.loc, child);
  }

  // -- Methods --
  
  /**
   * Remove (invalidate) cached content all directories.
   */
  public static void invalidateCache() {
    ome.scifio.io.CaseInsensitiveLocation.invalidateCache();
  }

  /**
   * Remove (invalidate) cached content for the specified directory.
   * @param dir the directory to remove,
   */
  public static void invalidateCache(File dir) {
    ome.scifio.io.CaseInsensitiveLocation.invalidateCache(dir);
  }
}
