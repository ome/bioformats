//
// IniWriter.java
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple writer for INI configuration files.
 *
 * <dl><dt><b>Source code:</b></dt>
 * <dd><a href="http://trac.openmicroscopy.org.uk/ome/browser/bioformats.git/components/common/src/loci/common/IniWriter.java">Trac</a>,
 * <a href="http://git.openmicroscopy.org/?p=bioformats.git;a=blob;f=components/common/src/loci/common/IniWriter.java;hb=HEAD">Gitweb</a></dd></dl>
 *
 * @author Melissa Linkert melissa at glencoesoftware.com
 */
public class IniWriter {

  /** Logger for this class. */
  private static final Logger LOGGER = LoggerFactory.getLogger(IniWriter.class);

  // -- IniWriter API methods --

  /**
   * Saves the given IniList to the given file.
   * If the given file already exists, then the IniList will be appended.
   */
  public void saveINI(IniList ini, String path) throws IOException {
    saveINI(ini, path, true);
  }

  /** Saves the given IniList to the given file. */
  public void saveINI(IniList ini, String path, boolean append)
    throws IOException
  {
    BufferedWriter out = new BufferedWriter(
      new OutputStreamWriter(new FileOutputStream(path, append), "UTF-8"));

    for (IniTable table : ini) {
      String header = table.get(IniTable.HEADER_KEY);
      out.write("[" + header + "]\n");
      for (String key : table.keySet()) {
        out.write(key + " = " + table.get(key) + "\n");
      }
      out.write("\n");
    }

    out.close();
  }

}
